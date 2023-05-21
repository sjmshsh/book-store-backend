package com.bookback.service;

import com.alipay.api.AlipayApiException;
import com.bookback.model.AlipayDo;

public interface PayService {
    String aliPay(AlipayDo aliPayBean) throws AlipayApiException;
}