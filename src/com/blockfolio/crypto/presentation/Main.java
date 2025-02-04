package com.blockfolio.crypto.presentation;

import com.blockfolio.crypto.domain.contract.CoinGeckoApiService;
import com.blockfolio.crypto.domain.dto.CoinDetailsDto;
import com.blockfolio.crypto.domain.impl.CoinGeckoService;
import java.util.List;

public class Main {

    private static CoinGeckoApiService coinGeckoApiService;

    public static void main(String[] args) {
        coinGeckoApiService = new CoinGeckoService();
        /*
        List<CoinDto> search = coinGeckoApiService.searchCoinByNameOrId("bitcoin");
        if (search.isEmpty()) {
            System.out.println("Такої монети не існує");
        }
        for (CoinDto coinDto : search) {
            System.out.println(coinDto);
        }
        */


        List<CoinDetailsDto> pageCoins = coinGeckoApiService.getCoinWithPage(50,1);
        for (CoinDetailsDto coin : pageCoins) {
            System.out.println(coin);
        }
        /*
        List<CoinDetailsDto> pageCoins2 = coinGeckoApiService.getCoinWithPage(50,2);
        for (CoinDetailsDto coin : pageCoins2) {
            System.out.println(coin);
        }
        */


    }
}
