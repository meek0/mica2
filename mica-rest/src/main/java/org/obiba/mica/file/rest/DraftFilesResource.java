package org.obiba.mica.file.rest;

import com.google.common.eventbus.EventBus;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.file.event.IndexFilesEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Component
@RequiresAuthentication
@Path("/draft")
public class DraftFilesResource {

  final private EventBus eventBus;

  @Inject
  public DraftFilesResource(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @PUT
  @Path("/files/_index")
  public Response index() {
    eventBus.post(new IndexFilesEvent());
    return Response.noContent().build();
  }
}
