package ro.go.adrhc.springbootkstreamstutorial.infrastructure.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;

import static ro.go.adrhc.springbootkstreamstutorial.util.DateUtils.format;

@Service
@Slf4j
public class PhoneMessageSenderImpl implements PhoneMessageSender {
	private final AppProperties app;

	public PhoneMessageSenderImpl(AppProperties app) {
		this.app = app;
	}

	@Override
	public void send(DailyExceeded de) {
		DailyTotalSpent dts = de.getDailyTotalSpent();
		log.debug("\n\tNotification:\t{} {} spent a total of {} {} on {}\n\tOverdue:\t{} {}\n\tLimit:\t\t{} {}",
				de.getName(), de.getSurname(), dts.getAmount(), app.getCurrency(), format(dts.getTime()),
				dts.getAmount() - de.getDailyMaxAmount(), app.getCurrency(),
				de.getDailyMaxAmount(), app.getCurrency());
	}
}
