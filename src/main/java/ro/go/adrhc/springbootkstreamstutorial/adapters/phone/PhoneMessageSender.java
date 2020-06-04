package ro.go.adrhc.springbootkstreamstutorial.adapters.phone;

import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);
}
