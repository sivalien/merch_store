package com.merch_store.controller.dto;

import java.util.List;


public record CoinHistoryResponse(List<ReceivedEntry> received,
                                  List<SentEntry> sent) {
}
