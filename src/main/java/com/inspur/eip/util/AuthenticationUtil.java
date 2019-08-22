package com.inspur.eip.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationUtil {


    public Boolean isAuthority (String orderType,String orderRoute,String token){
        if( orderRoute.equals("EIP")){
            if(orderType.equals("new")){

            } else if (orderType.equals("changeConfigure")) {

            }else if(orderType.equals("unsubscribe")){

            }
        } else {

            if(orderType.equals("new")){

            }else if(orderType.equals("changeConfigure")){

            }else{

            }

        }
        return true;
    }
}
