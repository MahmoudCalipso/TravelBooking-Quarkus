package com.travelplatform.interfaces.rest.controller.cms;

import com.travelplatform.interfaces.rest.controller.admin.AdminAnalyticsController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * CMS analytics endpoints for SUPER_ADMIN.
 */
@Path("/api/v1/cms/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("SUPER_ADMIN")
public class CmsAnalyticsController extends AdminAnalyticsController {
}
