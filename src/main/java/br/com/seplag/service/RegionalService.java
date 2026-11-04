package br.com.seplag.service;

import br.com.seplag.exception.IntegracaoException;
import br.com.seplag.model.Regional;
import br.com.seplag.repository.RegionalRepository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegionalService {
    private final RegionalRepository repository;
    private final RestClient cliente;
    private final String url;

    public RegionalService(RegionalRepository repository, @Value("${app.regionais-url}") String url) {
        this.repository = repository; this.url = url; this.cliente = RestClient.create();
    }
    public record RegionalResponse(Integer id, Integer codigoExterno, String nome, boolean ativo) {
        static RegionalResponse of(Regional r) {
            return new RegionalResponse(r.getId(), r.getCodigoExterno(), r.getNome(), r.isAtivo());
        }
    }
    public record ResultadoSync(int novos, int inativados, int alterados) {}
    @Transactional(readOnly = true)
    public List<RegionalResponse> listar() {
        return repository.findByAtivoTrue().stream().map(RegionalResponse::of).toList();
    }
    @Transactional
    public ResultadoSync sincronizar() {
        JsonNode raiz;
        try {
            raiz = cliente.get().uri(url).retrieve().body(JsonNode.class);
        } catch (Exception ex) {
            throw new IntegracaoException("Não foi possível consultar regionais externas", ex);
        }
        JsonNode itens = raiz;
        if (raiz != null && raiz.isObject()) {
            itens = raiz.has("data") ? raiz.get("data") : raiz.get("regionais");
        }
        if (itens == null || !itens.isArray())
            throw new IntegracaoException("Formato inesperado na resposta de regionais", null);

        Map<Integer, String> externos = new HashMap<>();
        for (JsonNode item : itens) {
            if (!item.hasNonNull("id") || !item.hasNonNull("nome"))
                throw new IntegracaoException("Regional externa sem id ou nome", null);
            int codigo = item.get("id").asInt();
            String nome = item.get("nome").asText().trim();
            if (nome.isEmpty() || nome.length() > 200)
                throw new IntegracaoException("Nome de regional externa inválido", null);
            externos.put(codigo, nome);
        }
        Map<Integer, Regional> atuais = repository.findByAtivoTrue().stream()
            .collect(Collectors.toMap(Regional::getCodigoExterno, r -> r));

        int inativados = 0, alterados = 0, novos = 0;
        List<Regional> inserir = new ArrayList<>();
        for (Regional local : atuais.values()) {
            String nomeExterno = externos.remove(local.getCodigoExterno());
            if (nomeExterno == null) {
                local.inativar();
                inativados++;
            } else if (!local.getNome().equals(nomeExterno)) {
                local.inativar();
                inserir.add(new Regional(local.getCodigoExterno(), nomeExterno, true));
                alterados++;
            }
        }
        repository.flush(); // desativa versões antigas antes das novas inserções
        for (Map.Entry<Integer, String> novo : externos.entrySet()) {
            inserir.add(new Regional(novo.getKey(), novo.getValue(), true));
            novos++;
        }
        repository.saveAll(inserir);
        return new ResultadoSync(novos, inativados, alterados);
    }
}
