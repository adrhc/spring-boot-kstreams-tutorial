package ro.go.adrhc.springbootkstreamstutorial.infrastructure.phone;

import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);
}
