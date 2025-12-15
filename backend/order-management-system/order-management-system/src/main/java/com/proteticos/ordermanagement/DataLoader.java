package com.proteticos.ordermanagement;

import com.proteticos.ordermanagement.model.Dentista;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.repository.DentistaRepository;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Override
    public void run(String... args) throws Exception {
        criarDentistasTeste();
        criarProteticosTeste();
        System.out.println("✅ Sistema carregado com dados de teste!");
    }

    private void criarDentistasTeste() {
        if (dentistaRepository.count() > 0) return;

        // Dentista 1
        Dentista dentista1 = new Dentista(
                "Dr. João Silva",
                "joao@clinica.com",
                "123456",  // Senha
                "SP12345",
                "Ortodontia"
        );
        dentista1.setTelefone("(11) 9999-8888");
        dentista1.setEnderecoClinica("Rua das Flores, 123 - São Paulo");

        // Dentista 2
        Dentista dentista2 = new Dentista(
                "Dra. Maria Santos",
                "maria@clinica.com",
                "123456",
                "SP67890",
                "Implante"
        );
        dentista2.setTelefone("(11) 9777-6666");
        dentista2.setEnderecoClinica("Av. Paulista, 1000 - São Paulo");

        dentistaRepository.save(dentista1);
        dentistaRepository.save(dentista2);

        System.out.println("🎉 2 Dentistas de teste criados!");
    }

    private void criarProteticosTeste() {
        if (proteticoRepository.count() > 0) return;

        // Protético 1 - Aceita terceirização (Zircônia/Coroa)
        Protetico protetico1 = new Protetico(
                "Carlos Lab",
                "carlos@lab.com",
                "123456",  // Senha (NÃO senhaHash)
                "PRT123",
                "Zircônia"
        );
        protetico1.setTelefone("(11) 9888-7777");
        protetico1.setAceitaTerceirizacao(true);
        protetico1.setTaxaMinimaTerceirizacao(BigDecimal.valueOf(35.00));
        protetico1.setNotaTerceirizacao(BigDecimal.valueOf(4.5));
        protetico1.setQuantidadeTerceirizacoes(10);

        HashSet<TipoServico> especialidades1 = new HashSet<>();
        especialidades1.add(TipoServico.ZIRCONIA);
        especialidades1.add(TipoServico.COROA);
        protetico1.setEspecialidadesTerceirizacao(especialidades1);

        // Protético 2 - NÃO aceita terceirização
        Protetico protetico2 = new Protetico(
                "Ana Protética",
                "ana@lab.com",
                "123456",
                "PRT456",
                "Resina"
        );
        protetico2.setTelefone("(11) 9777-6666");
        protetico2.setAceitaTerceirizacao(false);

        // Protético 3 - Aceita terceirização (Implantes/Zircônia)
        Protetico protetico3 = new Protetico(
                "Lab Implantes",
                "implantes@lab.com",
                "123456",
                "PRT789",
                "Implantes"
        );
        protetico3.setTelefone("(11) 9666-5555");
        protetico3.setAceitaTerceirizacao(true);
        protetico3.setTaxaMinimaTerceirizacao(BigDecimal.valueOf(40.00));
        protetico3.setNotaTerceirizacao(BigDecimal.valueOf(4.8));
        protetico3.setQuantidadeTerceirizacoes(15);

        HashSet<TipoServico> especialidades3 = new HashSet<>();
        especialidades3.add(TipoServico.IMPLANTE);
        especialidades3.add(TipoServico.ZIRCONIA);
        protetico3.setEspecialidadesTerceirizacao(especialidades3);

        proteticoRepository.save(protetico1);
        proteticoRepository.save(protetico2);
        proteticoRepository.save(protetico3);

        System.out.println("🎉 3 Protéticos de teste criados!");
    }
}