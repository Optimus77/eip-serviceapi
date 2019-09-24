package com.inspur.eip.util.common;

import com.inspur.eip.entity.fw.Firewall;
import com.inspur.eip.exception.KeycloakTokenException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openstack4j.api.OSClient;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;
@RunWith(PowerMockRunner.class)
public class CommonUtilTest {
    @Mock
    private OSClient.OSClientV3 os2;
    @Before
    public void setUp(){

    }

    @Test
    public void getAllFireWall(){
        List<Firewall> result = CommonUtil.getAllFireWall();
    }

    @Test
    public void getUserId() throws KeycloakTokenException {
        try {
            String result = CommonUtil.getUserId();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void getDate(){
        String result = CommonUtil.getDate();
    }
    @Test
    public void getOsClientV3Util() throws KeycloakTokenException {
        try {
            OSClient.OSClientV3 result = CommonUtil.getOsClientV3Util("123");
        }catch ( Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void getOsClientV3Util2() throws KeycloakTokenException {
        try {
            OSClient.OSClientV3 result = CommonUtil.getOsClientV3Util("cn-north","Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJlYmFhNTY3Mi1iOWM0LTQ2MzQtOWMxNS01NGMzNTI3ZDY4OTUiLCJleHAiOjE1NjI1ODIyMjQsIm5iZiI6MCwiaWF0IjoxNTYyNTc2ODI0LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImlhYXMtc2VydmVyIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYjg0ZTBmODQtZjRhOC00NDU2LTgzNWQtYTRjNjE3OWYwMDQzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBQ0NPVU5UX0FETUlOIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfSwicmRzLW15c3FsLWFwaSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiIiLCJwcm9qZWN0IjoibGlzaGVuZ2hhbyIsImdyb3VwcyI6WyIvZ3JvdXAtbGlzaGVuZ2hhbyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaXNoZW5naGFvIiwiZW1haWwiOiJsaXNoZW5naGFvQGluc3B1ci5jb20ifQ.e743AsxR1MGxGThl-CdH3Rf4TKZtsZrdg0NWvA5G8jZQEp_1S8p32U7t7STv6Km2JvVr13LvoAD9spT5Nu1foOx_jPvoeAUXDfyKNHPlFlUynOYDOAQ53n8pgOYqqAsRsasGa-_SbEzgHzP7zf4u7n_8eGK116-Dm1wOty3orBVQtCLmPRIoLy_rQxWzNx7sxNGoZybsr99vg3h62JnCRVbSaaiivpWjJdiXmJ042vGJk5B452qhkoQMQUJNbS0rYFIoK_xiem1XrJJ5gkjWgCLBXBZ62pocpgLWTnqTpKqa8KLJziIJlYIsMHei0JYxncbXxkxWR5iWEZW0tO1qXA");
        }catch ( Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void getProjectName() throws KeycloakTokenException {
        String result = CommonUtil.getProjectName("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJlYmFhNTY3Mi1iOWM0LTQ2MzQtOWMxNS01NGMzNTI3ZDY4OTUiLCJleHAiOjE1NjI1ODIyMjQsIm5iZiI6MCwiaWF0IjoxNTYyNTc2ODI0LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImlhYXMtc2VydmVyIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYjg0ZTBmODQtZjRhOC00NDU2LTgzNWQtYTRjNjE3OWYwMDQzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBQ0NPVU5UX0FETUlOIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfSwicmRzLW15c3FsLWFwaSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiIiLCJwcm9qZWN0IjoibGlzaGVuZ2hhbyIsImdyb3VwcyI6WyIvZ3JvdXAtbGlzaGVuZ2hhbyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaXNoZW5naGFvIiwiZW1haWwiOiJsaXNoZW5naGFvQGluc3B1ci5jb20ifQ.e743AsxR1MGxGThl-CdH3Rf4TKZtsZrdg0NWvA5G8jZQEp_1S8p32U7t7STv6Km2JvVr13LvoAD9spT5Nu1foOx_jPvoeAUXDfyKNHPlFlUynOYDOAQ53n8pgOYqqAsRsasGa-_SbEzgHzP7zf4u7n_8eGK116-Dm1wOty3orBVQtCLmPRIoLy_rQxWzNx7sxNGoZybsr99vg3h62JnCRVbSaaiivpWjJdiXmJ042vGJk5B452qhkoQMQUJNbS0rYFIoK_xiem1XrJJ5gkjWgCLBXBZ62pocpgLWTnqTpKqa8KLJziIJlYIsMHei0JYxncbXxkxWR5iWEZW0tO1qXA");

    }
    @Test
    public void isParentOrChildAccount() throws KeycloakTokenException {
        boolean result = CommonUtil.isParentOrChildAccount("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJlYmFhNTY3Mi1iOWM0LTQ2MzQtOWMxNS01NGMzNTI3ZDY4OTUiLCJleHAiOjE1NjI1ODIyMjQsIm5iZiI6MCwiaWF0IjoxNTYyNTc2ODI0LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImlhYXMtc2VydmVyIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYjg0ZTBmODQtZjRhOC00NDU2LTgzNWQtYTRjNjE3OWYwMDQzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBQ0NPVU5UX0FETUlOIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfSwicmRzLW15c3FsLWFwaSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiIiLCJwcm9qZWN0IjoibGlzaGVuZ2hhbyIsImdyb3VwcyI6WyIvZ3JvdXAtbGlzaGVuZ2hhbyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaXNoZW5naGFvIiwiZW1haWwiOiJsaXNoZW5naGFvQGluc3B1ci5jb20ifQ.e743AsxR1MGxGThl-CdH3Rf4TKZtsZrdg0NWvA5G8jZQEp_1S8p32U7t7STv6Km2JvVr13LvoAD9spT5Nu1foOx_jPvoeAUXDfyKNHPlFlUynOYDOAQ53n8pgOYqqAsRsasGa-_SbEzgHzP7zf4u7n_8eGK116-Dm1wOty3orBVQtCLmPRIoLy_rQxWzNx7sxNGoZybsr99vg3h62JnCRVbSaaiivpWjJdiXmJ042vGJk5B452qhkoQMQUJNbS0rYFIoK_xiem1XrJJ5gkjWgCLBXBZ62pocpgLWTnqTpKqa8KLJziIJlYIsMHei0JYxncbXxkxWR5iWEZW0tO1qXA");

    }
}
