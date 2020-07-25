package jpabook.jpashop.Domain.Item;

public class NotEnoughStockException extends Throwable {
    public NotEnoughStockException(String need_more_stock) {
    }
}
