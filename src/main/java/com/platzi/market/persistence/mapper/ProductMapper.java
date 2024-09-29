package com.platzi.market.persistence.mapper;

import com.platzi.market.domain.Product;
import com.platzi.market.persistence.entity.Producto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    @Mappings({
            @Mapping(source = "idProducto", target = "productId"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "idCategoria", target = "categoryId"),
            @Mapping(source = "precioVenta", target = "price"),
            @Mapping(source = "cantidadStock", target = "stock"),
            @Mapping(source = "estado", target = "active"),
            @Mapping(source = "categoria", target = "category"),
    })
    Product toProduct(Producto producto);

    List<Product> toProducts(List<Producto> productos);

    @InheritInverseConfiguration
    @Mapping(target = "codigoBarras", ignore = true)
    Producto toProduct(Product product);

    // Nuevos métodos para manejar Page
    default Page<Product> toProductPage(Page<Producto> productoPage) {
        List<Product> products = toProducts(productoPage.getContent());
        return new PageImpl<>(products, productoPage.getPageable(), productoPage.getTotalElements());
    }

    default Page<Producto> toProductoPage(Page<Product> productPage) {
        List<Producto> productos = productPage.getContent().stream()
                .map(this::toProduct)
                .collect(Collectors.toList());
        return new PageImpl<>(productos, productPage.getPageable(), productPage.getTotalElements());
    }
}
