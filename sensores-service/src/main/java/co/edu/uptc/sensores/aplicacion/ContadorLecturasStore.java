package co.edu.uptc.sensores.aplicacion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Persists consecutive out-of-range counts so a service restart does not reset them. */
@Component
public class ContadorLecturasStore {
    private final Path file;

    public ContadorLecturasStore(@Value("${sensores.contadores.archivo:./data/lecturas-consecutivas.properties}") String file) {
        this.file = Path.of(file).toAbsolutePath();
    }

    public synchronized int incrementar(UUID envioId, UUID sensorId) {
        Properties values = leer();
        String key = key(envioId, sensorId);
        int next = Integer.parseInt(values.getProperty(key, "0")) + 1;
        values.setProperty(key, Integer.toString(next));
        guardar(values);
        return next;
    }

    public synchronized void reiniciar(UUID envioId, UUID sensorId) {
        Properties values = leer();
        if (values.remove(key(envioId, sensorId)) != null) guardar(values);
    }

    private String key(UUID envioId, UUID sensorId) {
        return envioId + "." + sensorId;
    }

    private Properties leer() {
        Properties values = new Properties();
        if (!Files.exists(file)) return values;
        try (InputStream input = Files.newInputStream(file)) {
            values.load(input);
            return values;
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible leer el contador persistido de sensores", e);
        }
    }

    private void guardar(Properties values) {
        Path parent = file.getParent();
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(parent);
            try (OutputStream output = Files.newOutputStream(temporary)) {
                values.store(output, "Contadores consecutivos de lecturas fuera de rango");
            }
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible guardar el contador persistido de sensores", e);
        }
    }
}
