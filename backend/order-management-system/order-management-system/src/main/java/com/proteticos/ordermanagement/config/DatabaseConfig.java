import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

@Configuration  // ← Adicionar esta anotação
public class DatabaseConfig {

    private final JdbcTemplate jdbcTemplate;  // ← Tornar final e usar injeção

    // Injeção via construtor
    @Autowired
    public DatabaseConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct  // ← Esta anotação faz o Spring executar automaticamente
    public void init() {
        try {
            System.out.println("INICIALIZANDO BANCO DE DADOS...");
            // Sua lógica de inicialização aqui

            System.out.println("REMOVENDO tabelas existentes...");  // ← Corrigido o typo (se houver)
            // jdbcTemplate.execute("DROP TABLE IF EXISTS ...");

            System.out.println("CRIANDO tabelas...");
            // jdbcTemplate.execute("CREATE TABLE ...");

            System.out.println("INSERINDO dados iniciais...");
            // jdbcTemplate.update("INSERT INTO ...");

        } catch (Exception e) {
            System.err.println("ERRO na inicialização do banco: " + e.getMessage());
        }
    }
}