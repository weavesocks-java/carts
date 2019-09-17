package com.oracle.coherence.weavesocks.cart;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.helidon.grpc.core.MarshallerSupplier;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.RpcService;
import io.helidon.microprofile.grpc.core.Unary;

import com.oracle.coherence.helidon.io.PofMarshaller;
import com.oracle.io.pof.PortableTypeSerializer;
import com.oracle.io.pof.SimplePofContext;
import com.oracle.io.pof.annotation.PortableList;
import com.oracle.io.pof.annotation.PortableType;
import com.tangosol.net.NamedCache;
import io.grpc.MethodDescriptor;

@RpcService
@ApplicationScoped
@GrpcMarshaller("carts")
public class CartService {
    @Inject
    private NamedCache<String, Cart> carts;

    @Unary
    public CartResponse getCart(String cartId) {
        Cart cart = carts.get(cartId);
        return new CartResponse(cart);
    }

    // ---- inner class: CartResponse ---------------------------------------

    @PortableType(id = 2)
    public static class CartResponse {
        @PortableList(elementClass = Item.class)
        private List<Item> items;

        public CartResponse(Cart cart) {
            this.items = cart.items();
        }
    }

    // ---- inner class: Marshaller -----------------------------------------

    @ApplicationScoped
    @Named("carts")
    public static class Marshaller implements MarshallerSupplier {

        private final MethodDescriptor.Marshaller<?> marshaller;

        @SuppressWarnings("Duplicates")
        public Marshaller() {
            SimplePofContext ctx = new SimplePofContext()
                    .registerPortableTypes(Item.class, CartResponse.class);
            marshaller = new PofMarshaller(ctx);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> MethodDescriptor.Marshaller<T> get(Class<T> aClass) {
            return (MethodDescriptor.Marshaller<T>) marshaller;
        }
    }
}
