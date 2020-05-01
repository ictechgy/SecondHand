package org.jh.secondhand.ui.sell;

import android.os.Handler;

public class SellRepository {

    private static SellRepository instance;
    private SellDataSource sellDataSource;

    private SellRepository(SellDataSource sellDataSource){
        this.sellDataSource = sellDataSource;
    }

    public static SellRepository getInstance(SellDataSource sellDataSource){
        if(instance == null){
            instance = new SellRepository(sellDataSource);
        }
        return instance;
    }

    public void getList(final Handler handler){
        sellDataSource.getList(handler);
    }

}
