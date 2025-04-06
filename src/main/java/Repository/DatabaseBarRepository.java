package Repository;


import Model.Bar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface DatabaseBarRepository extends CrudRepository<Bar,Long> {
    Optional<Bar> findByTickerAndCikAndDate(String ticker, String cik, Date date);
    //lList<Bar> findAllByTickerAndCikAndDateAfterAndDateBefore(String ticker, String cik, Date after, Date before);
    List<Bar> findAllByCikAndTickerAndDateAfterAndDateBefore(String cik, String ticker, Date after, Date before);
    List<Bar> findAllByDateAfterAndDateBefore(Date after, Date before);
    List<Bar> findAllByDateAfterAndDateBeforeAndCikAndTicker( Date after, Date before,String cik, String ticker);
    @Query("SELECT DISTINCT bar.ticker,bar.cik FROM Bar bar")
    List<String> findAllUniqueTickers();
    @Query("SELECT bar from Bar bar")
    List<Bar> findAllByCikAndTicker(String cik,String ticker);


}
