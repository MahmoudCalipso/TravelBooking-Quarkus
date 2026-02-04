package com.travelplatform.interfaces.rest.controller.cms;

import com.travelplatform.interfaces.rest.controller.admin.AdminApprovalController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * CMS approval endpoints for SUPER_ADMIN.
 */
@Path("/api/v1/cms/approvals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("SUPER_ADMIN")
public class CmsApprovalController extends AdminApprovalController {
}
