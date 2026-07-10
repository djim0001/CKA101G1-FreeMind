package freeMind;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * No Spring context loaded (no DB/Redis needed) - directly exercises the
 * exact HttpSessionRequestCache / SavedRequest classes used by
 * MemberSecurityConfig to reproduce the "like already applied" report.
 */
public class RequestCacheReplayTest {

    @Test
    void savedPostRequestIsNotReplayedAsPostOnRedirectFollow() {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

        // Hop 1: original unauthenticated POST to the like endpoint
        MockHttpServletRequest originalPost = new MockHttpServletRequest("POST", "/member/article/5/like");
        originalPost.setQueryString("page=1&catId=2");
        originalPost.setServerName("localhost");
        originalPost.setServerPort(8080);
        MockHttpServletResponse response1 = new MockHttpServletResponse();

        requestCache.saveRequest(originalPost, response1);

        SavedRequest saved = requestCache.getRequest(originalPost, response1);
        assertNotNull(saved, "request should have been saved");
        System.out.println("Saved request method = " + saved.getMethod());
        System.out.println("Saved redirect URL   = " + saved.getRedirectUrl());

        // Hop 3: what the browser actually sends after the 302 that
        // SavedRequestAwareAuthenticationSuccessHandler issues to
        // saved.getRedirectUrl() - browsers downgrade POST->GET on redirect.
        // Use the ACTUAL redirect URL Spring computed (it appends "&continue").
        String redirectUrl = saved.getRedirectUrl();
        String queryString = redirectUrl.substring(redirectUrl.indexOf('?') + 1);

        MockHttpServletRequest replayedGet = new MockHttpServletRequest("GET", "/member/article/5/like");
        replayedGet.setQueryString(queryString);
        replayedGet.setServerName("localhost");
        replayedGet.setServerPort(8080);
        replayedGet.setSession(originalPost.getSession());
        MockHttpServletResponse response2 = new MockHttpServletResponse();

        System.out.println("replayedGet.getQueryString() = " + replayedGet.getQueryString());
        System.out.println("buildFullRequestUrl = " + org.springframework.security.web.util.UrlUtils.buildFullRequestUrl(replayedGet));
        var uriComponents = org.springframework.web.util.UriComponentsBuilder.newInstance().query(replayedGet.getQueryString()).build();
        System.out.println("parsed query params = " + uriComponents.getQueryParams());
        System.out.println("containsKey continue = " + uriComponents.getQueryParams().containsKey("continue"));

        var matched = requestCache.getMatchingRequest(replayedGet, response2);
        System.out.println("getMatchingRequest() result = " + matched);
        if (matched != null) {
            System.out.println("wrapped request method = " + matched.getMethod());
            System.out.println("wrapped request param page = " + matched.getParameter("page"));
        }
    }
}
