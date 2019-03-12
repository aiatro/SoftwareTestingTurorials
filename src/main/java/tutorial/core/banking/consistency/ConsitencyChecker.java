package tutorial.core.banking.consistency;

import java.net.ConnectException;

import tutorial.core.banking.data.IRepository;
import tutorial.core.banking.infrastructure.IMessageSender;

public class ConsitencyChecker<TUnit extends IUnit> {

	private IMessageSender messageSender;
	private IRule<TUnit>[] rules;
	private IRepository<TUnit> repository;
	
	public ConsitencyChecker(IMessageSender messageSender, IRepository<TUnit> repository, IRule<TUnit>...rules) {
		
		this.messageSender=messageSender;
		this.rules=rules;
		this.repository=repository;
		
	}
	
	public void check() {
		
		TUnit[] units = repository.getAll();
		
		for(TUnit unit : units) {
			check(unit);
		}
		
	}

	private void check(TUnit unit) {
		
		
		for(IRule<TUnit> rule : rules) {
			Violation[] violations =  rule.check(unit);
			
			reportViolations(violations);
		}
		
	}

	private void reportViolations(Violation[] violations) {
		
		for(Violation violation : violations) {
			try {
				messageSender.SendMessage("data_team@company.ca", "Data Violation", violation.toString());
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
