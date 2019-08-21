package com.oracle.coherence.weavesocks.cart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.tangosol.net.NamedCache;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Aleksandar Seovic  2019.08.20
 */
@ApplicationScoped
@Path("/carts")
public class CartResource {

    @Inject
    private NamedCache<String, Cart> carts;

    @GET
    @Path("{customerId}")
    @Produces(APPLICATION_JSON)
    public Cart getCart(@PathParam("customerId") String customerId) {
        return carts.getOrDefault(customerId, new Cart(customerId));
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCart(@PathParam("customerId") String customerId) {
        carts.remove(customerId);
        return Response.accepted().build();
    }

    @GET
    @Path("{customerId}/merge")
    public Response mergeCarts(@PathParam("customerId") String customerId,
                               @QueryParam("sessionId") String sessionId) {
        final Cart sessionCart = carts.get(sessionId);
        if (sessionCart != null) {
            carts.invoke(customerId, entry -> {
                Cart cart = entry.getValue();
                if (cart == null) {
                    cart = new Cart(entry.getKey());
                }
                entry.setValue(cart.merge(sessionCart));
                return cart;
            });
            carts.remove(sessionId);
            return Response.accepted().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("{customerId}/items")
    public ItemResource getItems(@PathParam("customerId") String customerId) {
        return new ItemResource(carts, getCart(customerId));
    }
}
