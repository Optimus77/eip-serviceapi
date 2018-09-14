package com.inspur.eip.util;

import com.inspur.icp.common.util.Base64Util;
import com.inspur.icp.common.util.OSClientUtil;
import lombok.Getter;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class CommonUtil {
    public static String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    private final static Logger log = Logger.getLogger(CommonUtil.class.getName());

    private static String authUrl = "https://10.110.25.117:5000/v3"; //endpoint Url
    private static String user = "vpc";
    private static String password = "123456a?";
    @Getter
    private static String projectId = "65a859f362f749ce95237cbd08c30edf";
    private static String userDomainId = "default";
    private static Config config = Config.newConfig().withSSLVerificationDisabled();

    public static OSClientV3 getOsClientV3(){
        String token = getKeycloackToken();
        return OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(user, password, Identifier.byId(userDomainId))
                .withConfig(config)
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();
    }
    public static OSClientV3 getOsClientV3Util(){
        String token = getKeycloackToken();
        log.info(token);
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String project = (String) jsonObject.get("project");
        return OSClientUtil.getOSClientV3("10.110.25.117",token,project);
    }


    public static String getKeycloackToken(){
        //important
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String keyCloackToken  = (String) request.getHeader("authorization");
        return keyCloackToken;
    }
}
