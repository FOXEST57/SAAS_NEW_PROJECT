package com.mns.cda.saas_facturation.Iservice;

public interface IOrderLineService {

    public static class OrderLineNotFoundException extends Exception {}

    public  static class InsufficientStockException extends Exception {}

}
