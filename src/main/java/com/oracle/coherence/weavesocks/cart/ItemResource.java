package com.oracle.coherence.weavesocks.cart;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.tangosol.net.NamedCache;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Aleksandar Seovic  2019.08.20
 */
public class ItemResource {

    private final NamedCache<String, Cart> carts;
    private final Cart cart;

    public ItemResource(NamedCache<String, Cart> carts, Cart cart) {
        this.carts = carts;
        this.cart = cart;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Item> getItems() {
        return cart.items();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response addItem(Item item) {
        Item result = carts.invoke(cart.customerId, entry -> {
            Cart cart = entry.getValue();
            if (cart == null) {
                cart = new Cart(entry.getKey());
            }
            entry.setValue(cart.add(item));
            return cart.getItem(item.itemId());
        });
        return Response
                .status(Status.CREATED)
                .entity(result)
                .build();
    }

    @GET
    @Path("{itemId}")
    @Produces(APPLICATION_JSON)
    public Item getItem(@PathParam("itemId") String itemId) {
        return cart.getItem(itemId);
    }

    @DELETE
    @Path("{itemId}")
    public Response deleteItem(@PathParam("itemId") String itemId) {
        carts.invoke(cart.customerId, entry -> {
            Cart cart = entry.getValue();
            entry.setValue(cart.remove(itemId));
            return null;
        });
        return Response.accepted().build();
    }

    @PATCH
    @Path("{itemId}")
    @Consumes(APPLICATION_JSON)
    public Response updateItem(@PathParam("itemId") String itemId, Item item) {
        carts.invoke(cart.customerId, entry -> {
            Cart cart = entry.getValue();
            entry.setValue(cart.update(item));
            return null;
        });
        return Response.accepted().build();
    }
}
