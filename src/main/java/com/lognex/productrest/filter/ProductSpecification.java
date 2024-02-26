package com.lognex.productrest.filter;

import com.lognex.productrest.entity.Product;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.EnumSet;

public class ProductSpecification implements Specification<Product> {
    private static final EnumSet<Operation> NULL_OPERATIONS = EnumSet.of(Operation.NULL, Operation.NOT_NULL);
    private final CriteriaModel criteriaModel;

    public ProductSpecification(CriteriaModel criteriaModel) {
        checkCriteria(criteriaModel);
        this.criteriaModel = criteriaModel;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        checkCriteria(criteriaModel);

        Operation operation = criteriaModel.getOperation();
        Path<Object> expression = root.get(criteriaModel.getField());
        String value = criteriaModel.getValue();

        switch (operation) {
            case NULL -> {
                return criteriaBuilder.isNull(expression);
            }
            case NOT_NULL -> {
                return criteriaBuilder.isNotNull(expression);
            }
            case EQ -> {
                return criteriaBuilder.equal(expression, value);
            }
            case LIKE -> {
                String likeString = "%" + value + "%";
                return criteriaBuilder.like(expression.as(String.class), likeString);
            }
            case GT -> {
                if ("price".equals(criteriaModel.getField())) {
                    return criteriaBuilder.gt(expression.as(BigDecimal.class), BigDecimal.valueOf(Long.parseLong(value)));
                }
            }
            case LT -> {
                if ("price".equals(criteriaModel.getField())) {
                    return criteriaBuilder.lt(expression.as(BigDecimal.class), BigDecimal.valueOf(Long.parseLong(value)));
                }
            }
            case GE -> {
                if ("price".equals(criteriaModel.getField())) {
                    return criteriaBuilder.ge(expression.as(BigDecimal.class), BigDecimal.valueOf(Long.parseLong(value)));
                }
            }
            case LE -> {
                if ("price".equals(criteriaModel.getField())) {
                    return criteriaBuilder.le(expression.as(BigDecimal.class), BigDecimal.valueOf(Long.parseLong(value)));
                }
            }
        }
        return null;
    }

    private void checkCriteria(CriteriaModel criteriaModel){
        if (criteriaModel == null) {
            throw new IllegalArgumentException("CriteriaModel is null");
        }
        if (StringUtils.isBlank(criteriaModel.getField())) {
            throw new IllegalArgumentException("Field must be not null");
        }
        Operation operation = criteriaModel.getOperation();
        if (operation == null) {
            throw new IllegalArgumentException("Operation must be not null");
        }
        if (!NULL_OPERATIONS.contains(operation) && criteriaModel.getValue() == null) {
            throw new IllegalArgumentException("Value must be not null");
        }

    }
}
