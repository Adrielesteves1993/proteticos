// service/ConviteService.java - VERSÃO COMPLETA
package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.model.Convite;
import com.proteticos.ordermanagement.model.UserTipo;
import com.proteticos.ordermanagement.model.Usuario;
import com.proteticos.ordermanagement.repository.ConviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConviteService {

    @Autowired
    private ConviteRepository conviteRepository;

    public Convite criarConvite(UserTipo tipo, String emailConvidado, Usuario criadoPor) {
        // Verificar limite de convites (ex: 10 por mês)
        long convitesAtivos = conviteRepository.countByCriadoPorIdAndUtilizadoFalse(criadoPor.getId());
        if (convitesAtivos >= 10) {
            throw new RuntimeException("Limite de convites atingido");
        }

        String codigo = gerarCodigoUnico(tipo);

        Convite convite = new Convite();
        convite.setCodigo(codigo);
        convite.setTipo(tipo);
        convite.setEmailConvidado(emailConvidado);
        convite.setCriadoPor(criadoPor); // ← IMPORTANTE: setar o criadoPor
        convite.setCriadoEm(LocalDateTime.now());
        convite.setExpiraEm(LocalDateTime.now().plusDays(30));
        convite.setUtilizado(false);

        return conviteRepository.save(convite);
    }

    public Optional<Convite> validarConvite(String codigo) {
        Optional<Convite> conviteOpt = conviteRepository.findByCodigoAndUtilizadoFalse(codigo);
        if (conviteOpt.isPresent()) {
            Convite convite = conviteOpt.get();
            if (convite.getExpiraEm().isAfter(LocalDateTime.now())) {
                return Optional.of(convite);
            }
        }
        return Optional.empty();
    }

    public void marcarComoUtilizado(Convite convite, Usuario utilizadoPor) {
        convite.setUtilizado(true);
        convite.setUtilizadoEm(LocalDateTime.now());
        convite.setUtilizadoPor(utilizadoPor);
        conviteRepository.save(convite);
    }

    private String gerarCodigoUnico(UserTipo tipo) {
        String codigo;
        do {
            String prefixo = tipo == UserTipo.DENTISTA ? "DENT" : "PROT";
            // Gera código sem usar UUID para o ID
            String randomPart = generateRandomString(8);
            codigo = "PTLAB-" + prefixo + "-" + randomPart;
        } while (conviteRepository.existsByCodigo(codigo));

        return codigo;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return result.toString();
    }
}