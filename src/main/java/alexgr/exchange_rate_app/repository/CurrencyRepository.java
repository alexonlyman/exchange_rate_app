package alexgr.exchange_rate_app.repository;

import alexgr.exchange_rate_app.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для сохранения сущностей в базе данных
 */
@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
    // поиск данных по коду сущности
    CurrencyEntity findByCurrencyCode(String currencyCode);


}
