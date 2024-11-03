package com.musinsa.productmanageserver.event.message;

public record BrandUpdateEvent (
    String oldBrandName,
    String newBrandName
){

}
