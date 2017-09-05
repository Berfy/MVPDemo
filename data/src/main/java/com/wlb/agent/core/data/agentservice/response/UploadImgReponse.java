package com.wlb.agent.core.data.agentservice.response;

import com.wlb.agent.core.data.base.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */

public class UploadImgReponse extends BaseResponse {
    private List<String> imgUrls;
    public List<String> getImgUrls(){
        if(null==imgUrls){
            imgUrls=new ArrayList<>();
        }
        return imgUrls;
    }
}
