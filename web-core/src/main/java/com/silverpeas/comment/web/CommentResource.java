/*
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection withWriter Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along withWriter this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.silverpeas.comment.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.net.URI;
import java.util.List;
import com.silverpeas.comment.CommentRuntimeException;
import com.silverpeas.comment.model.Comment;
import com.silverpeas.comment.model.CommentPK;
import com.silverpeas.comment.service.CommentService;
import com.silverpeas.rest.RESTWebService;
import java.util.Comparator;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import static com.silverpeas.util.StringUtil.*;

/**
 * A REST Web resource representing a given comment.
 * It is a web service that provides an access to a comment referenced by its URL.
 */
@Service
@Scope("request")
@Path("comments/{componentId}/{contentId}")
public class CommentResource extends RESTWebService {

  @Inject
  private CommentService commentService;
  @PathParam("componentId")
  private String componentId;
  @PathParam("contentId")
  private String contentId;

  /**
   * Gets the JSON representation of the specified existing comment.
   * If the comment doesn't exist, a 404 HTTP code is returned.
   * If the user isn't authentified, a 401 HTTP code is returned.
   * If the user isn't authorized to access the comment, a 403 is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @param onCommentId the unique identifier of the comment.
   * @return the response to the HTTP GET request with the JSON representation of the asked comment.
   */
  @GET
  @Path("{commentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public CommentEntity getComment(@PathParam("commentId") String onCommentId) {
    checkUserPriviledges();
    try {
      Comment theComment = commentService().getComment(byPK(onCommentId, inComponentId()));
      URI commentURI = getUriInfo().getRequestUri();
      return asWebEntity(theComment, identifiedBy(commentURI));
    } catch (CommentRuntimeException ex) {
      throw new WebApplicationException(ex, Status.NOT_FOUND);
    } catch (Exception ex) {
      throw new WebApplicationException(ex, Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Gets the JSON representation of all the comments on refered the resource.
   * If the user isn't authentified, a 401 HTTP code is returned.
   * If the user isn't authorized to access the comment, a 403 is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @return the response to the HTTP GET request with the JSON representation of the comments on
   * the refered resource.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public CommentEntity[] getAllComments() {
    checkUserPriviledges();
    try {
      List<Comment> theComments = commentService().getAllCommentsOnPublication(
          byPK(onContentId(), inComponentId()));
      return asWebEntities(theComments);
    } catch (CommentRuntimeException ex) {
      throw new WebApplicationException(ex, Status.NOT_FOUND);
    } catch (Exception ex) {
      throw new WebApplicationException(ex, Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Creates a new comment from its JSON representation and returns it with its URI identifying it
   * in Silverpeas.
   * The unique identifier of the comment isn't taken into account, so if the comment already exist,
   * it is then cloned with a new identifier (thus with a new URI).
   * If the user isn't authentified, a 401 HTTP code is returned.
   * If the user isn't authorized to save the comment, a 403 is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @param commentToSave the comment to save in Silverpeas.
   * @return the response to the HTTP PÖST request with the JSON representation of the saved comment.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveNewComment(final CommentEntity commentToSave) {
    checkUserPriviledges();
    checkIsValid(commentToSave);
    Comment comment = commentToSave.toComment();
    try {
      if (commentToSave.isIndexed()) {
        commentService().createAndIndexComment(comment);
      } else {
        commentService().createComment(comment);
      }
      Comment savedComment = commentService().getComment(comment.getCommentPK());
      URI commentURI = getUriInfo().getRequestUriBuilder().path(savedComment.getCommentPK().getId()).
          build();
      return Response.created(commentURI).
          entity(asWebEntity(savedComment, identifiedBy(commentURI))).build();
    } catch (CommentRuntimeException ex) {
      throw new WebApplicationException(ex, Status.CONFLICT);
    } catch (Exception ex) {
      throw new WebApplicationException(ex, Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Updates the comment from its JSON representation and returns it once updated.
   * If the comment to update doesn't match with the requested one, a 400 HTTP code is returned.
   * If the comment doesn't exist, a 404 HTTP code is returned.
   * If the user isn't authentified, a 401 HTTP code is returned.
   * If the user isn't authorized to save the comment, a 403 is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @param commentId the unique identifier of the comment to update.
   * @param commentToUpdate the comment to update in Silverpeas.
   * @return the response to the HTTP PUT request with the JSON representation of the updated
   * comment.
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("{commentId}")
  public CommentEntity updateComment(@PathParam("commentId") String commentId,
      final CommentEntity commentToUpdate) {
    checkUserPriviledges();
    checkIsValid(commentToUpdate);
    if (!commentToUpdate.getId().equals(commentId)) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }
    Comment comment = commentToUpdate.toComment();
    try {
      if (commentToUpdate.isIndexed()) {
        commentService().updateAndIndexComment(comment);
      } else {
        commentService().updateComment(comment);
      }
      URI commentURI = getUriInfo().getRequestUriBuilder().path(comment.getCommentPK().getId()).
          build();
      return asWebEntity(comment, identifiedBy(commentURI));
    } catch (CommentRuntimeException ex) {
      throw new WebApplicationException(ex, Status.NOT_FOUND);
    } catch (Exception ex) {
      throw new WebApplicationException(ex, Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Deletes the specified existing comment.
   * If the comment doesn't exist, a 404 HTTP code is returned.
   * If the user isn't authentified, a 401 HTTP code is returned.
   * If the user isn't authorized to access the comment, a 403 is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @param onCommentId the unique identifier of the comment to delete.
   */
  @DELETE
  @Path("{commentId}")
  public void deleteComment(@PathParam("commentId") String onCommentId) {
    checkUserPriviledges();
    try {
      commentService().deleteComment(byPK(onCommentId, inComponentId()));
    } catch (CommentRuntimeException ex) {
      throw new WebApplicationException(ex, Status.NOT_FOUND);
    } catch (Exception ex) {
      throw new WebApplicationException(ex, Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Gets the identifier of the Silverpeas instance to which the commented content belongs.
   * @return the Silverpeas component instance identifier.
   */
  protected String inComponentId() {
    return getComponentId();
  }

  /**
   * Gets the identifier of the content that is commentable.
   * @return the identifier of the commentable content.
   */
  protected String onContentId() {
    return contentId;
  }

  /**
   * Gets a business service on comments.
   * @return a comment service instance.
   */
  protected CommentService commentService() {
    return commentService;
  }

  /**
   * Gets the primary key of the specified resource.
   * @param id the unique identifier of the resource.
   * @param componentId the unique identifier of the component instance to which the resource
   * belongs.
   * @return the primary key of the resource.
   */
  protected CommentPK byPK(final String id, final String componentId) {
    return new CommentPK(id, componentId);
  }

  /**
   * Converts the specified list of comments into their corresponding web entities.
   * @param comments the comments to convert.
   * @return an array with the corresponding comment entities.
   */
  protected CommentEntity[] asWebEntities(List<Comment> comments) {
    CommentEntity[] entities = new CommentEntity[comments.size()];
    for (int i = 0; i < comments.size(); i++) {
      Comment comment = comments.get(i);
      URI commentURI = getUriInfo().getRequestUriBuilder().path(comment.getCommentPK().getId()).
          build();
      entities[i] = asWebEntity(comment, identifiedBy(commentURI));
    }
    return entities;
  }

  /**
   * Converts the comment into its corresponding web entity.
   * @param comment the comment to convert.
   * @param commentURI the URI of the comment.
   * @return the corresponding comment entity.
   */
  protected CommentEntity asWebEntity(final Comment comment, URI commentURI) {
    // TODO REMOVE THIS SPECIFIC AVATAR URL SETTING ONCE IN A MORE RECENT APPLICATION SERVER.
    // PLEASE SEE CommentAuthorEntity constructor TODO.
    CommentEntity entity = CommentEntity.fromComment(comment).withURI(commentURI);
    CommentAuthorEntity author = entity.getAuthor();
    author.setAvatar(getHttpServletContext().getContextPath() + author.getAvatar());
    return entity;
  }

  protected URI identifiedBy(URI uri) {
    return uri;
  }

  @Override
  protected String getComponentId() {
    return this.componentId;
  }

  /**
   * Check the specified comment is valid. A comment is valid if the following attributes are
   * set: componentId, resourceId, text and its author identifier.
   * @param theComment the comment to validate.
   */
  protected void checkIsValid(final CommentEntity theComment) {
    if (!isDefined(theComment.getComponentId()) || !isDefined(theComment.getResourceId()) ||
        !isDefined(theComment.getText()) || !isDefined(theComment.getAuthor().getId())) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }
  }

  /**
   * Gets a comparator of comments by their identifier, from the lower to the higher one.
   * @return a comparator of comments.
   */
  protected static Comparator<Comment> byId() {
    return new Comparator<Comment>() {

      @Override
      public int compare(Comment left, Comment right) {
        return left.getCommentPK().getId().compareTo(right.getCommentPK().getId());
      }

    };
  }
}
