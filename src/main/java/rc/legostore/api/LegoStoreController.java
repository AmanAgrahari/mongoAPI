package rc.legostore.api;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import rc.legostore.model.LegoSet;
import rc.legostore.model.LegoSetDifficulty;
import rc.legostore.model.QLegoSet;
import rc.legostore.persistence.LegoSetRepository;

import java.util.Collection;

@RestController
@RequestMapping("legostore/api")
public class LegoStoreController {

    /*
    private MongoTemplate mongoTemplate;

    public LegoStoreController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    */

    private LegoSetRepository legoSetRepository;

    public LegoStoreController(LegoSetRepository legoSetRepository) {
        this.legoSetRepository = legoSetRepository;
    }

    @PostMapping
    public void insert(@RequestBody LegoSet legoSet){
     //   this.mongoTemplate.insert(legoSet);
        this.legoSetRepository.insert(legoSet);
    }

    @PutMapping
    public void update(@RequestBody LegoSet legoSet){
        //     this.mongoTemplate.save(legoSet);
        this.legoSetRepository.save(legoSet);
    }

    @DeleteMapping("/{id}")
    public void update(@PathVariable String id){
       // this.mongoTemplate.remove(new Query(Criteria.where("id").is(id)),LegoSet.class);
        this.legoSetRepository.deleteById(id);
    }

    @GetMapping("/all")
    public Collection<LegoSet> all(){
        //this.mongoTemplate.findAll(LegoSet.class);
        Sort sortByThemeAsc = Sort.by("theme").ascending();
       return this.legoSetRepository.findAll(sortByThemeAsc);
    }

    @GetMapping("/{id}")
    public LegoSet byId(@PathVariable String id){
        return this.legoSetRepository.findById(id).orElse(null);
    }

    @GetMapping("/byTheme/{theme}")
    public  Collection<LegoSet> byTheme(@PathVariable String theme){
        Sort sortByTheme = Sort.by(theme).ascending();
        return this.legoSetRepository.findAllByThemeContains(theme,sortByTheme);
    }


    @GetMapping("hardThatStartWithM")
    public  Collection<LegoSet> hardThatStartWithM(){
      return this.legoSetRepository.findAllByDifficultyAndNameStartingWith(LegoSetDifficulty.HARD,"M");
    }

    @GetMapping("/byDeliveryFee/{price}")
    public  Collection<LegoSet> byDeliveryFeeLessThan(@PathVariable int price){
        return this.legoSetRepository.findAllByDeliveryPriceLessThan(price);
    }

    @GetMapping("greatReviews")
    public  Collection<LegoSet> ByGreatReviews(){
        return this.legoSetRepository.findAllByGreatReviews();
    }

    @GetMapping("bestBuys")
    public Collection<LegoSet> bestBuying(){
        //build the query
        QLegoSet query = new QLegoSet("query");
        BooleanExpression inStockFilter = query.deliveryInfo.inStock.isTrue();
        Predicate smallDEliveryFilter = query.deliveryInfo.deliveryFee.lt(50);
        Predicate hasGreatReviews = query.reviews.any().rating.eq(10);

        Predicate bestBuysFilter = inStockFilter.
                and(smallDEliveryFilter)
                .and(hasGreatReviews);

        // pass the query to findAll Method
        return (Collection<LegoSet>) this.legoSetRepository.findAll(bestBuysFilter);
    }
}
