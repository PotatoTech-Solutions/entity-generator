package com.potatotech.testesbackend_gen;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SpecificationFilter<T> {


    public Specification<T> customFilter(String filter){


        Specification<T> spec = (root, query, cb) -> {
            if (filter == null || filter.isEmpty()) {
                return null;
            }

            var and = filter.split("and");
            var or = filter.split("or");

            Arrays.stream(and).forEach(item -> {
                setEq(cb, item, root);
                setBetween(cb, item, root);
            });
            return cb.conjunction();
        };
        return spec;
    }

    private void setBetween(CriteriaBuilder cb, String item, Root<T> root) {

    }

    private void setEq(CriteriaBuilder cb, String item, Root<T> root) {

        var criteria = item.split("eq");
        if(criteria.length == 2){
            cb.and(cb.equal(cb.lower(root.get(criteria[0])), "%".concat(criteria[1]).concat("%")));
        }
    }


}
