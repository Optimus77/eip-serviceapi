package com.inspur.eip.util;

public class HsConstants {
	public static final String LANG = "zh_CN";
    public static final String ECS = "1";
    public static final String CPS = "2";
    public static final String SLB = "3";
    public static final String VERSION_REST = "/v2.0";
	public static final String IS_SBW = "is_SBW";
	public static final String YES = "yes";
	public static final String WITH_IPV6 = "withIpv6";
    public static final String PAYSUCCESS = "paySuccess";
    public static final String NOTFOUND = "NOT_FOUND";
	public static final String CREATESUCCESS = "createSuccess";
    public static final String HOURLYSETTLEMENT = "hourlySettlement";
    public static final String MONTHLY = "monthly";
    public static final String PROVIDER = "provider";
	public static final String BANDWIDTH = "bandwidth";
	public static final String FAIL = "fail";
	public static final String BINDING ="BINDING";
	public static final String UNSUBSCRIBE = "unsubscribe";
	public static final String STOPSERVER = "stopServer";
	public static final String RESUMESERVER = "resumeServer";
	public static final String CHARGE_MODE_BANDWIDTH = "Bandwidth";
	public static final String CHARGE_MODE_SHAREDBANDWIDTH = "SharedBandwidth";

	//	orderType
	public static final String NEW_ORDERTYPE = "new";
	public static final String CHANGECONFIGURE_ORDERTYPE = "changeConfigure";
	public static final String UNSUBSCRIBE_ORDERTYPE = "unsubscribe";
	public static final String RENEW_ORDERTYPE = "renew";

	public static final String SUCCESS = "success";
	public static final String DELETE = "DELETE";
	public static final String DELETED = "deleted";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String AUTHORIZATION = "Authorization";
	public static final String BILLTYPE = "billType";
	public static final String HILLTONE_LANGUAGE = "Hillstone-language";
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	public static final String APPLICATION_JSON = "application/json";
	public static final String KEYCLOAK_TOKEN_SUBPATH = "/realms/picp/protocol/openid-connect/token";
	public static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	public static final String FROM_ROOT_SYS = "fromrootvsys=true;";
	public static final String REST_LOGIN = "/rest/login";
	//SBW
	public static final String SBW_NAME= "sbwName";
	public static final String SBW_ID= "sbwId";

	//product route
	public static final String EIP = "EIP";
	public static final String SBW = "SBW";
	public static final String IPTS = "IPTS";


	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_DOWN = "DOWN";
	public static final String STATUS_BIND_ERROR = "BIND_ERROR";
	public static final String STATUS_BINDING = "BINDING";
	public static final String STATUS_STOP = "STOP";
	public static final String STATUS_PENDING_CREATE = "PENDING_CREATE";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_DELETE = "DELETE";

	public static final String REST_SNAT = "/rest/Snat";
	public static final String REST_DNAT = "/rest/Dnat";
	
	public static final String REST_DNAT_UPDATE = "?target=dnat_rule";
	public static final String REST_SNAT_ADD_UPDATE_DELETE = "?target=snat_rule";


	
	public static final int STATUS_CODE_200 = 200;
	public static final int STATUS_CODE_204 = 204;
	public static final int STATUS_CODE_404 = 404;

	public static final String TH_0 = "0";
	public static final String TH_1 = "1";
	public static final String TH_2 = "2";
	public static final String TH_3 = "3";
	public static final String TH_4 = "4";
	public static final String TH_5 = "5";
	public static final String TH_6 = "6";
	public static final String TH_7 = "7";
	public static final String TH_8 = "8";
	public static final String TH_9 = "9";
	public static final String TH_10 = "10";
	public static final String TH_11 = "11";
	public static final String TH_17 = "17";
	public static final String TH_1500 = "1500";

	public static final String ITEM_0 = "0";
	public static final String ITEM_1 = "1";
	public static final String ITEM_2 = "2";
	public static final String ITEM_3 = "3";
	public static final String ITEM_4 = "4";
	public static final String ITEM_5 = "5";
	public static final String ITEM_6 = "6";
	public static final String ITEM_7 = "7";
	public static final String ITEM_8 = "8";
	public static final String ITEM_9 = "9";
	public static final String ITEM_10 = "10";
	public static final String ITEM_11 = "11";
	public static final String ITEM_12 = "12";
	public static final String ITEM_13 = "13";
	public static final String ITEM_14 = "14";
	public static final String ITEM_15 = "15";
	public static final String ITEM_16 = "16";
	public static final String ITEM_17 = "17";
	public static final String ITEM_18 = "18";
	public static final String ITEM_19 = "19";
	public static final String ITEM_20 = "20";
	public static final String ITEM_21 = "21";
	public static final String ITEM_22 = "22";
	public static final String ITEM_23 = "23";
	public static final String ITEM_24 = "24";

	public static final String OPERATION_100 = "100";
	public static final String OPERATION_101 = "101";
	public static final String OPERATION_102 = "102";
	public static final String SERVICE_TCP = "TCP";
	public static final String SERVICE_UDP = "UDP";
	public static final String SERVICE_ICMP = "ICMP";
	public static final String SERVICE_OTHER = "Other";
	
	public static final String MAN_INAME = "ethernet0/0";
	public static final String FUN_INAME = "ethernet0/1";
	public static final String CONN_INAME = "ethernet0/2";
	public static final String MASK = "24";
	
	public static final String VR_NAME = "trust-vr";
	public static final String DEFAULT_NEXTHOP = "172.23.10.254";
	
	/////////////////////////R2P2 version/////////////////////////

	public static final String COLON = ":";
	public static final String SHAREDBANDWIDTH = "SharedBandwidth";
	public static final String FORBIDEN = "Forbiden";
	public static final String EXCEPTION = "exception";
	public static final String ACTIVE = "ACTIVE";
	public static final String DOWN = "DOWN";
	public static final String STOP = "STOP";
	public static final String ERROR = "ERROR";
	public static final String SCHEDULETIME = "scheduleTime";

	public static final String TOTAL_PAGES = "totalPages";
	public static final String TOTAL_ELEMENTS = "totalElements";
	public static final String CURRENT_PAGE = "currentPage";
	public static final String CURRENT_PAGEPER = "currentPagePer";
	public static final String RESULT = "result";
	public static final String PIPE_NAME= "pipeName";
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	public static final String BAND_WIDTH = "bandWidth";
	public static final String IN_BAND_WIDTH = "inBandWidth";
	public static final String REASON = "reason";
	public static final String HTTP_CODE = "httpCode";
	public static final String INTER_CODE = "interCode";
	public static final String REST_IQOS_ROOT = "/rest/iQos?target=root.rule";
	public static final String FLOATIP = "floatIp: ";
}
