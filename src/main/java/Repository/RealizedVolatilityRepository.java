package Repository;

import Model.ImpliedVolaility;
import Model.RealizedVolatility;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface RealizedVolatilityRepository extends CrudRepository<RealizedVolatility,Long> {

    Optional<RealizedVolatility> findByTickerAndDaysVolAndDate(String ticker, int daysVol, Date date);
}
