package cn.zjh.seckill.stock.application.model.command;

/**
 * 库存分桶Wrapper
 * 
 * @author zjh - kayson
 */
public class SeckillStockBucketWrapperCommand extends SeckillStockBucketGoodsCommand {

    private static final long serialVersionUID = 2920951547657301665L;

    // 库存分桶信息
    private SeckillStockBucketCommand stockBucketCommand;

    public SeckillStockBucketWrapperCommand() {
    }

    public SeckillStockBucketWrapperCommand(Long userId, Long goodsId, SeckillStockBucketCommand stockBucketCommand) {
        super(userId, goodsId);
        this.stockBucketCommand = stockBucketCommand;
    }

    public SeckillStockBucketCommand getStockBucketCommand() {
        return stockBucketCommand;
    }

    public void setStockBucketCommand(SeckillStockBucketCommand stockBucketCommand) {
        this.stockBucketCommand = stockBucketCommand;
    }

    public boolean isEmpty(){
        return this.stockBucketCommand == null
                || super.isEmpty()
                || stockBucketCommand.isEmpty();
    }
    
}
