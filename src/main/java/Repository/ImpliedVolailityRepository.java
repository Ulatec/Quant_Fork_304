package Repository;

import Model.ImpliedVolaility;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface ImpliedVolailityRepository extends CrudRepository<ImpliedVolaility,Long> {

    Optional<ImpliedVolaility> findByTickerAndDaysVolAndDate(String ticker, int daysVol, Date date);
    List<ImpliedVolaility> findByTickerAndDaysVol(String ticker, int daysVol);
}
