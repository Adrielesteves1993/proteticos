package com.proteticos.ordermanagement;

import com.proteticos.ordermanagement.model.Dentista;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.repository.DentistaRepository;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Criar dentistas de teste
        if (dentistaRepository.count() == 0) {
            Dentista dentista1 = new Dentista("Dr. João Silva", "joao@clinica.com", "123456", "SP12345", "Ortodontia");
            dentista1.setTelefone("(11) 9999-8888");

            Dentista dentista2 = new Dentista("Dra. Maria Santos", "maria@clinica.com", "123456", "SP67890", "Implante");
            dentista2.setTelefone("(11) 9777-6666");

            dentistaRepository.save(dentista1);
            dentistaRepository.save(dentista2);

            System.out.println("🎉 Dentistas de teste criados!");
        }

        // Criar protéticos de teste
        if (proteticoRepository.count() == 0) {
            Protetico protetico1 = new Protetico("Carlos Lab", "carlos@lab.com", "123456", "PRT123", "Zircônia");
            protetico1.setAceitaTerceirizacao(true);

            Protetico protetico2 = new Protetico("Ana Protética", "ana@lab.com", "123456", "PRT456", "Resina");
            protetico2.setAceitaTerceirizacao(false);

            proteticoRepository.save(protetico1);
            proteticoRepository.save(protetico2);

            System.out.println("🎉 Protéticos de teste criados!");
        }

        System.out.println("✅ Sistema carregado com dados de teste!");
    }
}