package com.example.czportalpage.common.exception.handler;

import com.example.czportalpage.common.exception.GeneralException;
import com.example.czportalpage.common.response.BaseErrorCode;

public class GeneralHandler extends GeneralException {
    public GeneralHandler(BaseErrorCode code) {
        super(code);
    }
}
