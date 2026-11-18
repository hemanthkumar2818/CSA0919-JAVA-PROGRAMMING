import java.util.List;
import java.util.Optional;

/** Generic Data Access Object contract. */
public interface Dao<T,K> {
    void insert(T item) throws Exception;
    Optional<T> findById(K id) throws Exception;
    List<T> findAll() throws Exception;
    void update(T item) throws Exception;
    void delete(K id) throws Exception;
}
