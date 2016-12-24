package com.library.app.order;

import com.library.app.order.model.Order;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;

public class OrderArgumentMatcher extends ArgumentMatcher<Order>{
    
    private Order expected;
    
    public static Order orderEq(Order expected){
        return argThat(new OrderArgumentMatcher(expected));
    }

    public OrderArgumentMatcher(Order expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object actualObj) {
        Order actual = (Order) actualObj;
        
        assertThat(actual.getId(), is(equalTo(expected.getId())));
		assertThat(actual.getCustomer(), is(equalTo(expected.getCustomer())));

		assertThat(actual.getItems(), is(equalTo(expected.getItems())));

		assertThat(actual.getTotal(), is(equalTo(expected.getTotal())));
		assertThat(actual.getHistoryEntries(), is(equalTo(expected.getHistoryEntries())));
		assertThat(actual.getCurrentStatus(), is(equalTo(expected.getCurrentStatus())));

        return true;
    }
    
}
