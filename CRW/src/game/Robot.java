package game;

import java.awt.Image;
import java.util.Stack;

public class Robot {
	String name;
	Program program;
	Image image;
	Image gun;


	int hull;
	int x;
	int y;
	int radius;
	int energy;

	int stasis;

	boolean touchingWall;
	boolean alive;
	boolean colliding;
	double aim;

	int processorSpeed;
	int maxEnergy;
	int maxShield;
	Arena arena;
	int shield;
	int chronons;
	int vx; //speedX
	int vy; //speedY
	boolean wasColliding;
	boolean wasOnWall;
	InterQueue interrupts;
	boolean wasAtTop;
	boolean wasAtBottom;
	boolean wasAtLeft;
	boolean wasAtRight;
	public double scan;
	int look;
	int last_ptr;
	int ptr;
	Stack<Object> stack;
	String bulletType;

	Robot(String n,Program p, Image i, Image g)
	{
		hull = 100;
		radius = 8;

		stasis = 0;

		touchingWall = false;
		alive = true;
		colliding = false;

		name = n;
		program = p;
		image = i;
		gun = g;
		processorSpeed = 10;
		chronons = 0;
		maxEnergy = 150;
		maxShield = 30;
		arena = null;  // Set later by the game

		// registers = {}; //TODO: add registers

		ptr = 0;
		last_ptr = 0;

		aim = 0;
		scan = 0;
		look = 0;
		energy = maxEnergy;
		shield = 0;
		stasis = 0;
		x = (int)(Math.random()*1000);
		y = (int)(Math.random()*1000);
		vx = 0;
		vy = 0;
		interrupts = new InterQueue();
		stack = new Stack<Object>();
		bulletType = "NORMAL";
	}

	public boolean equals(Robot other)
	{
		if(name == other.name)
		{
			return true;
		}
		return false;
	}

	/**
	 * Robot processes its instructions
	 */
	protected void step() {
		chronons++;
		//	System.out.println("------ " +  chronons + " ------");

		if ( stasis > 0) {
			stasis--;


			System.out.println("In stasis for " +  stasis + " more chronons");
			return;
		}

		if(aim > 360){
			aim = (aim%360);
		}

		energy = Math.min(maxEnergy, energy + 2);

		if ( touchingWall) 
		{
			takeDamage(5);
		}
		if ( colliding) 
		{
			takeDamage(1);
		}

		if (hull <= 0)
		{
			alive = false;
		}
		if (energy < -200) alive = false;
		if (hull <= 0)  colliding = false;
		if (energy < -200)  colliding = false;

		if (shield > maxShield)
		{
			shield = Math.max(0, shield - 2);
		}
		else if (shield > 0)
		{
			shield = (int) Math.max(0, shield - 1);
		}

		if (interrupts.enabled) {
			if (colliding) {
				if (!wasColliding) {
					interrupts.add("COLLISION");
					wasColliding = true;
				} else {
					wasColliding = false;
				}
			}
			if (touchingWall) {
				if (!wasOnWall) {
					interrupts.add("WALL");
					wasOnWall = true;
				} else {
					wasOnWall = false;
				}
			}

			if ( hull <  interrupts.getParam("DAMAGE"))
				interrupts.add("DAMAGE");
			if ( shield <  interrupts.getParam("SHIELD"))
				interrupts.add("SHIELD");

			if ( y <  interrupts.getParam("TOP")) {
				if (! wasAtTop) {
					interrupts.add("TOP");
					wasAtTop = true;
				}
			} else {
				wasAtTop = false;
			}
			if ( y >  interrupts.getParam("BOTTOM")) {
				if (! wasAtBottom) {
					interrupts.add("BOTTOM");
					wasAtBottom = true;
				}
			} else {
				wasAtBottom = false;
			}
			if ( x <  interrupts.getParam("LEFT")) {
				if (! wasAtLeft) {
					interrupts.add("LEFT");
					wasAtLeft = true;
				}
			} else {
				wasAtLeft = false;
			}
			if ( x >  interrupts.getParam("RIGHT")) {
				if (! wasAtRight) {
					interrupts.add("RIGHT");
					wasAtRight = true;
				}
			} else {
				wasAtRight = false;
			}

			checkRadarInterrupt();
			checkRangeInterrupt();

			// TODO: TEAMMATES interrupt. Teamplay not yet implemented.
			// TODO: SIGNAL interrupt. Teamplay not yet implemented.
			// TODO: ROBOTS interrupt.

			if ( chronons >=  interrupts.getParam("CHRONON")) {
				interrupts.add("CHRONON");
			}
		}

		for (int i =  processorSpeed; i > 0 &&  alive; ) {
			if ( energy <= 0) {
				//System.out.println("Robot has no energy");
				break;
			}
			try {
				if (interrupts.enabled && interrupts.hasNext()) {
					interrupts.enabled = false;
					String next = interrupts.next();
					//System.out.println("Executing interrupt " + next);
					opCall(interrupts.getPtr(next));
				}
				// Some instructions have no cost, like DEBUG, thus they return 0.
				i -= stepOne();
			} catch (Exception e) {
				int line = last_ptr;
				String instruction = program.instructions[last_ptr];
				String message = name + " error on line " + line + ", at " + instruction;
				System.out.println(message + "\n\n" + e);
				colliding = false;
				i-=1;
			}
		}

		int r =  radius;
		x = Math.max(r, Math.min( arena.width - r,  x +  vx));
		y = Math.max(r, Math.min( arena.height - r,  y +  vy));

		wasColliding =  colliding;
		wasOnWall =  touchingWall;
	}

	private int opCall(int address) 
	{
		int returnAddr = ptr;
		ptr = address;
		stack.push(returnAddr);
		return 1;
	}

	private int stepOne() 
	{
		String instruction = program.instructions[ptr];
		if (ptr >= program.numberOfInstructions)
			throw new Error("Program finished");
		if (instruction == null)
			throw new Error("Undefined instruction: " + instruction);

		last_ptr = ptr;
		ptr++;

		if (Game.isComment(instruction)){
			return 0;
		}
		else if (Game.isVariable(instruction)) {
			stack.push(instruction);
			return 1;
		} else if (Literal.isLiteral(instruction)) {
			int value = Integer.parseInt(instruction);
			stack.push(value);
			return 1;
		} else if (Operator.isOperator(instruction)) { //always true for now TODO: fix this
			return  handleOperation(instruction);
		}
		return 1;
	}

	private int handleOperation(String op) {
		//Stack<Object> s =  stack;

		switch (op) {
		case "+": return opApply2(op);

		case "-": return opApply2(op);

		case "*": return opApply2(op);

		case "/": return opApply2(op);

		case "=": return opApply2(op);

		case "!": return opApply2(op);

		case ">": return opApply2(op);

		case "<": return opApply2(op);
		//
		//		case "AND": return opApply2(op);
		//
		//		case "OR": return opApply2(op);
		//
		//		case "XOR": case "EOR": return opApply2(op);
		//
		//		case "ABS": return opApply1(op);
		//		case "CHS": return opApply1(op);
		//		case "MAX": return opApply2(op);
		//		case "MIN": return opApply2(op);
		//		case "MOD": return opApply2(op);
		//		case "NOT": return opApply1(op);
		//		case "SQRT": return opApply1(op);
		//
		//		case "SIN": case "SINE": return opTrig(op);
		//		case "COS": case "COSSINE": return opTrig(op);
		//		case "TAN": case "TANGENT": return opTrig(op);
		//		case "ARCSIN": return opTrig(op);
		//		case "ARCCOS": return opTrig(op);
		//		case "ARCTAN":
		//			int i =  popNumber();
		//			int j =  popNumber();
		//			double result = Math.atan2(-i, j);  // Flip Y coord.
		//			// Robowar"s Engine/Arena.c does this, so I will:
		//			 push((int)(450.5 - Arena.rad2deg(result)) % 360);
		//			return 1;
		//			//		case "DIST":
		//			//			int dy =  y -  pop_number();
		//			//			int dx =  x -  pop_number();
		//			//			return Math.sqrt( (dx * dx) + (dy * dy) );

		case "STORE":
		case "STO":
		case "EXEC":
			String v = popVariable();
			useVariable(v, popNumber());
			return 1;
			//		case "RECALL":
			//			 push( pop_variable_value());
			//			return 1;
			//		case "VEXEC":
			//			var index =  pop_number();
			//			var value =  pop_number();
			//			 vector[index] = value;
			//			return 1;
			//		case "VRECALL":
			//			var index =  pop_number();
			//			var value =  vector[index] || 0;
			//			 push((value < 0 || value > 100) ? 0 : value);
			//			return 1;
			//
		case "IF": //TODO: MAKE THIS WORK WITH VARIABLES
			int first = popNumber();
			int second = popNumber();
			if (second == 1) {
				return opCall(first);
			}
			return 1;
			//		case "IFE":
			//			var first =  pop_number();
			//			var second =  pop_number();
			//			var third =  pop_number();
			//			if (third) {
			//				return  op_call(second);
			//			} else {
			//				return  op_call(first);
			//			}
			//		case "IFG":
			//			var first =  pop_number();
			//			var second =  pop_number();
			//			if (second) {
			//				return  op_jump(first);
			//			}
			//			return 1;
			//		case "IFEG":
			//			var first =  pop_number();
			//			var second =  pop_number();
			//			var third =  pop_number();
			//			if (third) {
			//				return  op_jump(second);
			//			} else {
			//				return  op_jump(first);
			//			}
			//
			//		case "CALL":
			//			return  op_call( pop_number());
		case "JUMP":
		case "RETURN":
			return opJump(popNumber());

			//		case "NOP":
			//			return 1;
		case "SYNC":
			// To pause until end of chronon we return maximum "cost".
			return Integer.MAX_VALUE;
		case "DROP":
			stack.pop();
			return 1;
			//		case "DUP":
			//		case "DUPLICATE":
			//			var value =  pop_number();
			//			 stack.push(value);
			//			 stack.push(value);
			//			return 1;
		case "DROPALL":
			stack.clear();
			return 1;
			//		case "SWAP":
			//			var first =  pop_number();
			//			var second =  pop_number();
			//			 push(first);
			//			 push(second);
			//			return 1;
			//		case "ROLL":
			//			var count =  pop_number();
			//			var value =  pop_number();
			//			if (count >  stack.length)
			//				throw new Error("Tried rolling back " + count + " places, but " +
			//						"only " +  stack.length + " items are in the stack.");
			//			Stack temp = new Stack();
			//			for (var i = 0; i < count; i ++)
			//				temp.push( stack.pop());
			//			 stack.push(value);
			//			for (var i = 0; i < count; i ++)
			//				 stack.push(temp.pop());
			//			return 1;

		case "INTON":
			interrupts.enabled = true;
			return 1;
		case "INTOFF":
			interrupts.enabled = false;
			return 1;
		case "FLUSHINT":
			interrupts.flush();
			return 1;
		case "RTI": // Equivalent to INTON RETURN
			interrupts.enabled = true;
			opJump(popNumber());
			return 2;
		case "SETINT":
			String l = popVariable();
			int address = popNumber();
			interrupts.setPtr(l, address);
			return 1;
			//		case "SETPARAM":
			//			var v =  pop_variable();
			//			if (v.name == "HISTORY") {
			//				var value =  pop_number();
			//				 history_index = value;
			//			} else if (v.name == "PROBE") {
			//				var value =  pop_variable();
			//				 probe_variable = value;
			//			} else {
			//				var value =  pop_number();
			//				 interrupts.set_param(v.name, value);
			//			}
			//			return 1;
			//
		case "DEBUG":
		case "DEBUGGER":
			// TODO: Debugging.
			System.out.println(popNumber());
			return 0;
		case "BEEP":
			//System.out.println("BEEP!");
			return 0;
		case "PRINT":
			if (stack.size() > 0) {
				System.out.println("Stack size " + stack.size() + ", top value: " + stack.peek());
			} else {
				System.out.println("Stack is empty.");
			}
			return 0;
		case "GET":
			String p = popVariable();
			stack.push(getVariable(p));
			return 0;

		default:
			throw new Error("Unknown instruction: " + op);
		}
	}

	private int opJump(int popNumber) {
		int address = popNumber;
		//  trace('Go to',  program.address_to_label[address]);
		ptr = address;
		return 1;
	}

	private String popVariable() {
		if ( stack.size() == 0) {
			throw new Error("Stack empty");
		}
		Object value =  stack.pop();
		if (!(value instanceof String)) {
			throw new Error("Invalid value on stack: " + value + " is not a Variable");
		} else {
			return (String) value;
		}
	}

	private void useVariable(String v, int value) {
		switch (v) {
		case "AIM":
			aim += Arena.fix360(value);
			checkRadarInterrupt();
			checkRangeInterrupt();
			return;
			//      case "BULLET":
			//       if ( bullet_type == "EXPLOSIVE")
			//           shoot("NORMAL_BULLET", value);
			//        else
			//           shoot( bullet_type + "_BULLET", value);
			//        return;
		case "BOTTOM":
		case "BOT":
			return;
			//		case "CHANNEL":
			//			throw new Error("Teamplay not yet implemented");
			//		case "CHRONON":
			//		case "COLLISION":
			//		case "DAMAGE":
			//		case "DOPPLER":
			//			return;
			//		case "DRONE":
			//			throw new Error("Drones are not supported as of RoboWar 2.4");
		case "ENERGY":
			return;
		case "FIRE":
			shoot( bulletType, value);
			return;
			//		case "FRIEND":
			//			throw new Error("Teamplay not yet implemented");
			//		case "HISTORY":
			//			return;
			//		case "HELLBORE":
			//			 shoot(name, value);
			//			return;
			//		case "ICON0":
			//		case "ICON1":
			//		case "ICON2":
			//		case "ICON3":
			//		case "ICON4":
			//		case "ICON5":
			//		case "ICON6":
			//		case "ICON7":
			//		case "ICON8":
			//		case "ICON9":
			//			return;
			//		case "ID":
			//		case "KILLS":
			//			return;
			//		case "LASER":
			//			throw new Error("Lasers are not supported as of RoboWar 2.4");
		case "LEFT":
			return;
			//		case "LOOK":
			//			 checkRadarInterrupt();
			//			 look = value;
			//			return;
			//		case "MINE":
			//			 shoot(name, value);
			//			return;
			//		case "MISSILE":
			//			 shoot(name, value);
			//			return;
		case "MOVEX":
			teleport("x", value);
			return;
		case "MOVEY":
			teleport("y", value);
			return;
			//		case "NUKE":
			//			 shoot(name, value);
			//			return;
			//		case "PROBE":
			//		case "RADAR":
			//		case "RANDOM":
			//		case "RANGE":
		case "RIGHT":
			return;

			//		case "ROBOTS":
			//			return;
			//		case "SCAN":
			//			 checkRangeInterrupt();
			//			 scan = fix360(value);
			//			return;
		case "SHIELD":
			value = Math.max(0, value);
			if ( shield < value) {
				int cost = value -  shield;
				if ( energy < cost) {
					shield += ( energy);
					energy = 0;
				} else {
					shield = value;
					energy -= cost;
				}
			} else if ( shield > value) {
				int gain =  shield - value;
				shield = value;
				energy = Math.min( energy + gain,  maxEnergy);
			}
			return;
			//	      case "SIGNAL":
			//	        throw new Error("Teamplay not yet implemented");
			//	      case "SND0":
			//	      case "SND1":
			//	      case "SND2":
			//	      case "SND3":
			//	      case "SND4":
			//	      case "SND5":
			//	      case "SND6":
			//	      case "SND7":
			//	      case "SND8":
			//	      case "SND9":
			//	        return;
		case "SPEEDX":
			setSpeed("x", value);
			return;
		case "SPEEDY":
			setSpeed("y", value);
			return;
			//	      case "STUNNER":
			//	         shoot(name, value);
			//	        return;
			//	      case "TEAMMATES":
			//	        throw new Error("Teamplay not yet implemented");
		case "TOP":
		case "WALL":
			return;
		case "X":
		case "Y":
			throw new Error("Robot tried to teleport by setting X or Y");

			//default:
			//    registers[name] = value;
		}
	}

	int getVariable(String name) {
		// Resolve label names first. Some simpler bots have label names with the
		// same name as variables.
		//  if (name in  program.label_to_address) {
		//    return  program.label_to_address[name];
		//  }

		switch (name) {
		case "AIM":
			return (int) aim;
		case "BULLET":
			return 0;
		case "BOTTOM":
		case "BOT":
			return 0;
		case "CHANNEL":
			throw new Error("Teamplay not yet implemented");
		case "CHRONON":
			return chronons;
		case "COLLISION":
			return colliding ? 1 : 0;
		case "DAMAGE":
			return hull;
		case "DOPPLER":
			return (int) arena.doDoppler(this);
		case "ENERGY":
			return energy;
		case "FIRE":
			return 0;
			//case "FRIEND":
			//  throw new Error("Teamplay not yet implemented");
			//case "HISTORY":
			//  return  history[ history_index] || 0;
		case "HELLBORE":
			return 0;
			//	      case "ICON0":
			//	      case "ICON1":
			//	      case "ICON2":
			//	      case "ICON3":
			//	      case "ICON4":
			//	      case "ICON5":
			//	      case "ICON6":
			//	      case "ICON7":
			//	      case "ICON8":
			//	      case "ICON9":
			//	        return 0;
			// case "ID":
			//  return _.indexOf( arena.robots, this);
			// case "KILLS":
			//   throw new Error("TODO: get_variable(" + name + ")");
		case "LEFT":
			return 0;
		case "LOOK":
			return look;
		case "MINE":
		case "MISSILE":
		case "MOVEX":
		case "MOVEY":
		case "NUKE":
			return 0;
			// case "PROBE":
			//   return  do_probe();
		case "RADAR":
			return (int) arena.doRadar(this);
		case "RANDOM":
			return (int) (Math.random() * 360);
		case "RANGE":
			return arena.doRange(this);
		case "RIGHT":
			return 0;
		case "ROBOTS":
			return arena.activeRobots();
		case "SCAN":
			return (int) scan;
		case "SHIELD":
			return shield;
			// case "SIGNAL":
			//  throw new Error("TODO: get_variable(" + name + ")");
		case "SND0":
		case "SND1":
		case "SND2":
		case "SND3":
		case "SND4":
		case "SND5":
		case "SND6":
		case "SND7":
		case "SND8":
		case "SND9":
			return 0;
		case "SPEEDX":
			return  vx;
		case "SPEEDY":
			return  vy;
		case "STUNNER":
			return 0;
			// case "TEAMMATES":
			//   throw new Error("Teamplay not yet implemented");
		case "TOP":
			return 0;
		case "WALL":
			return  touchingWall ? 1 : 0;
		case "X":
			return x;
		case "Y":
			return y;

			//   default:
			//   if (name in  registers) {
			//      return  registers[name];
			//     }
			//
			// Allow for undefined A-Z registers.
			// if (name.match(/[A-Z]$/)) return 0;

			//   throw new Error("Unknown variable or label: " " + name + " ");
		}
		return Integer.MIN_VALUE; //something is wrong
	}

	private void teleport(String axis, int energy)
	{
		int distance = (int)(energy / 2);
		energy -= (int)(energy);
		int r =  radius;
		switch (axis) {
		case "x":
			x = Math.max(r, Math.min( arena.width - r,  x + distance));
			break;
		case "y":
			y = Math.max(r, Math.min( arena.height - r,  y + distance));
		}

	}

	private void setSpeed(String axis, int speedParam) 
	{
		int value = speedParam;
		if (Math.abs(value) > 10)//set max speed
		{
			if(value < 0){
				value = -10;
			}
			else
			{
				value = 10;
			}
		}
		switch (axis) {
		case "x":
			int differenceX = Math.abs(vx - value) * 2;
			energy -= differenceX;
			vx = value;
			break;
		case "y":
			int differenceY = Math.abs(vy - value) * 2;
			energy -= differenceY;
			vy = value;
		}
	}

	private void shoot(String type, int amount) {
		amount = Math.min(amount,  maxEnergy);
		arena.shoot(this, type, amount);
		energy -= amount;
	}

	private int popNumber() {
		if ( stack.size() == 0) {
			throw new Error("Stack empty");
		}
		Object value =  stack.pop();
		if (!(value instanceof Number)) {
			throw new Error("Invalid value on stack: " + value + " is not a Number");
		} else {
			return (int) value;
		}
	}

	private int opTrig(String op) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int opApply1(String op) {
		return 1;

	}

	private int opApply2(String op) {
		switch (op) {
		case "+": stack.push(popNumber() + popNumber()); return 1;
		case "-": stack.push(popNumber() - popNumber()); return 1;
		case "*": stack.push(popNumber() * popNumber()); return 1;
		case "/": stack.push((int)(popNumber() / popNumber())); return 1;
		case "=": stack.push(popNumber() == popNumber() ? 1 : 0); return 1;
		case "!": stack.push(popNumber() != popNumber() ? 1 : 0); return 1;
		case ">": stack.push(popNumber() > popNumber() ? 1 : 0); return 1;
		case "<": stack.push(popNumber() < popNumber() ? 1 : 0); return 1;

		//  case "AND": stack.push(popVariable() && popVariable() ? 1 : 0); return 1;
		//  case "OR": return  op_apply2(function(a, b) { return a || b ? 1 : 0 });
		}
		return 1;
	}

	protected boolean isTouching(Robot other)
	{
		return  distanceTo(other) <= 0;
	}

	protected boolean isTouching(Projectile other)
	{
		return  distanceTo(other) <= 0;
	}

	protected double distanceTo(Robot other) {
		if (other != null)
		{
			Robot a = this;
			Robot b = other;
			if (a == b)
			{
				return 0;
			}
			int dx = a.x - b.x;
			int dy = a.y - b.y;
			return (Math.sqrt( (dx * dx) + (dy * dy) ) - a.radius - b.radius - 1);
		}
		else
		{
			return 0;
		}
	}

	protected double distanceTo(Projectile other) {
		if (other != null)
		{
			Robot a = this;
			Projectile b = other;
			int dx = a.x - b.x;
			int dy = a.y - b.y;
			return (Math.sqrt( (dx * dx) + (dy * dy) ) - a.radius - b.radius - 1);
		}
		else
		{
			return 0;
		}
	}

	protected void takeDamage(int amount)
	{
		if (amount <= shield)
		{
			shield -= amount;
		} 
		else
		{
			int remainder = amount - shield;
			shield = 0;
			hull -= remainder;
		}
		//System.out.println(hull);
	}

	protected void checkRadarInterrupt() {
		double radar = arena.doRadar(this);
		if (radar != 0 && radar <= interrupts.getParam("RADAR"))
		{
			interrupts.add("RADAR");
		}
	}

	protected void checkRangeInterrupt() {
		int range = arena.doRange(this);
		if (range != 0 && range <= interrupts.getParam("RANGE"))
		{
			//System.out.println(range);
			interrupts.add("RANGE");
		}
	}
}
