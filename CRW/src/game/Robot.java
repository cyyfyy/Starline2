package game;

import java.awt.Image;
import java.util.Stack;

public class Robot {
	String name;
	Program program;
	Image image;


	int hull;
	int x;
	int y;
	int radius;
	int energy;

	int stasis;


	//	int speedX;
	//	int speedY;

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

	Robot(String n,Program p, Image i)
	{
		hull = 100;
		radius = 8;
		//energy = 100;

		stasis = 0;

		//	speedX = 0;
		//	speedY = 0;

		touchingWall = false;
		alive = true;
		colliding = false;

		name = n;
		// this.color = color;
		program = p;
		image = i;
		processorSpeed = 10;
		chronons = 0;
		maxEnergy = 150;
		maxShield = 30;
		//  this.set_trace(false);
		arena = null;  // Set later by Game.add_robot().

		//this.registers = {};
		//this.vector = [];
		//this.stack = [];
		ptr = 0;
		last_ptr = 0;

		//	this.probe_variable = new Variable('DAMAGE');
		//	this.history_index;
		//	this.history = _.range(50);

		aim = 90;
		scan = 0;
		look = 0;
		energy = maxEnergy;
		shield = 0;
		stasis = 0;
		this.x = (int)(Math.random()*1000);
		this.y = (int)(Math.random()*1000);
		this.vx = 0;
		this.vy = 0;
		this.interrupts = new InterQueue();
		this.stack = new Stack<Object>();
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
		//this.trace('------ ' + this.chronons + ' ------');
		//	System.out.println("------ " + this.chronons + " ------");

		if (this.stasis > 0) {
			this.stasis--;


			System.out.println("In stasis for " + this.stasis + " more chronons");
			// this.trace('In stasis for ' + this.stasis + ' more chronons');
			return;
		}

		if(aim > 360){
			aim = (aim%360);
		}

		energy = Math.min(maxEnergy, energy + 2);

		if (this.touchingWall) 
		{
			//System.out.println("wall damage");
			this.takeDamage(5);
		}
		if (this.colliding) 
		{
			this.takeDamage(1);
		}

		if (hull <= 0)
		{
			alive = false;
		}
		if (energy < -200) alive = false;
		// if (this.damage <= 0) this.colliding = false;
		//  if (this.energy < -200) this.colliding = false;

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

			if (this.hull < this.interrupts.getParam("DAMAGE")) //make separate damage field?
				this.interrupts.add("DAMAGE");
			if (this.shield < this.interrupts.getParam("SHIELD"))
				this.interrupts.add("SHIELD");

			if (this.y < this.interrupts.getParam("TOP")) {
				if (!this.wasAtTop) {
					this.interrupts.add("TOP");
					this.wasAtTop = true;
				}
			} else {
				this.wasAtTop = false;
			}
			if (this.y > this.interrupts.getParam("BOTTOM")) {
				if (!this.wasAtBottom) {
					this.interrupts.add("BOTTOM");
					this.wasAtBottom = true;
				}
			} else {
				this.wasAtBottom = false;
			}
			if (this.x < this.interrupts.getParam("LEFT")) {
				if (!this.wasAtLeft) {
					this.interrupts.add("LEFT");
					this.wasAtLeft = true;
				}
			} else {
				this.wasAtLeft = false;
			}
			if (this.x > this.interrupts.getParam("RIGHT")) {
				if (!this.wasAtRight) {
					this.interrupts.add("RIGHT");
					this.wasAtRight = true;
				}
			} else {
				this.wasAtRight = false;
			}

			checkRadarInterrupt();
			checkRangeInterrupt();

			// TODO: TEAMMATES interrupt. Teamplay not yet implemented.
			// TODO: SIGNAL interrupt. Teamplay not yet implemented.
			// TODO: ROBOTS interrupt.

			if (this.chronons >= this.interrupts.getParam("CHRONON")) {
				this.interrupts.add("CHRONON");
			}
		}

		for (int i = this.processorSpeed; i > 0 && this.alive; ) {
			if (this.energy <= 0) {
				//System.out.println("Robot has no energy");
				//this.trace("Robot has no energy");
				break;
			}
			try {
				if (interrupts.enabled && interrupts.hasNext()) {
					interrupts.enabled = false;
					String next = interrupts.next();
					// this.trace("Executing interrupt " + next);
					System.out.println("Executing interrupt " + next);
					opCall(interrupts.getPtr(next));
				}
				// Some instructions have no cost, like DEBUG, thus they return 0.
				i -= stepOne();
			} catch (Exception e) {
				int line = last_ptr;
				//int line = program.getLineNumbers()[last_ptr];
				String instruction = program.instructions[last_ptr];
				String message = name + " error on line " + line + ", at " + instruction;
				System.out.println(message + "\n\n" + e);
				this.colliding = false;
				i-=1;
			}
		}

		int r = this.radius;
		this.x = Math.max(r, Math.min(this.arena.width - r, this.x + this.vx));
		this.y = Math.max(r, Math.min(this.arena.height - r, this.y + this.vy));

		this.wasColliding = this.colliding;
		this.wasOnWall = this.touchingWall;
	}

	private int opCall(int address) 
	{
		// this.trace('Jumping to', this.program.address_to_label[address], 'with return');
		int returnAddr = ptr;
		ptr = address;
		stack.push(returnAddr);
		return 1;
	}

	private int stepOne() 
	{
		//this.debug_stack();
		String instruction = program.instructions[ptr];
		//System.out.println(instruction);
		if (ptr >= program.numberOfInstructions)
			throw new Error("Program finished");
		if (instruction == null)
			throw new Error("Undefined instruction");
		//		this.trace(
		//				pad('L' + this.program.lineNumbers[ptr], 6),
		//				pad("" + instruction.toString(), 15),
		//				this.debug_stack());

		last_ptr = ptr;
		ptr++;

		if (Game.isVariable(instruction)) {
			stack.push(instruction);
			return 1;
		} else if (Literal.isLiteral(instruction)) {
			int value = Integer.parseInt(instruction);
			stack.push(value);
			return 1;
		} else if (Operator.isOperator(instruction)) {
			return this.handleOperation(instruction);
		}
		return 1;
	}

	//	private void push(Object instructions)
	//	{
	//		if (instructions == null)
	//			throw new Error("null pushed onto the stack");
	//		this.stack.push(instructions);
	//		if (this.stack.size() > 100) {
	//			throw new Error("Stack overflow");
	//		}
	//	}

	private int handleOperation(String op) {
		Stack<Object> s = this.stack;

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
		//			int i = this.popNumber();
		//			int j = this.popNumber();
		//			double result = Math.atan2(-i, j);  // Flip Y coord.
		//			// Robowar"s Engine/Arena.c does this, so I will:
		//			this.push((int)(450.5 - Arena.rad2deg(result)) % 360);
		//			return 1;
		//			//		case "DIST":
		//			//			int dy = this.y - this.pop_number();
		//			//			int dx = this.x - this.pop_number();
		//			//			return Math.sqrt( (dx * dx) + (dy * dy) );

		case "STORE":
		case "STO":
		case "EXEC":
			String v = popVariable();
			useVariable(v, popNumber());
			return 1;
			//		case "RECALL":
			//			this.push(this.pop_variable_value());
			//			return 1;
			//		case "VEXEC":
			//			var index = this.pop_number();
			//			var value = this.pop_number();
			//			this.vector[index] = value;
			//			return 1;
			//		case "VRECALL":
			//			var index = this.pop_number();
			//			var value = this.vector[index] || 0;
			//			this.push((value < 0 || value > 100) ? 0 : value);
			//			return 1;
			//
		case "IF": //TODO: MAKE THIS WORK WITH VARIABLES
			int first = popNumber();
			int second = popNumber();
			if (second == 0) {
				return opCall(first);
			}
			return 1;
			//		case "IFE":
			//			var first = this.pop_number();
			//			var second = this.pop_number();
			//			var third = this.pop_number();
			//			if (third) {
			//				return this.op_call(second);
			//			} else {
			//				return this.op_call(first);
			//			}
			//		case "IFG":
			//			var first = this.pop_number();
			//			var second = this.pop_number();
			//			if (second) {
			//				return this.op_jump(first);
			//			}
			//			return 1;
			//		case "IFEG":
			//			var first = this.pop_number();
			//			var second = this.pop_number();
			//			var third = this.pop_number();
			//			if (third) {
			//				return this.op_jump(second);
			//			} else {
			//				return this.op_jump(first);
			//			}
			//
			//		case "CALL":
			//			return this.op_call(this.pop_number());
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
			//			var value = this.pop_number();
			//			this.stack.push(value);
			//			this.stack.push(value);
			//			return 1;
		case "DROPALL":
			stack.clear();
			return 1;
			//		case "SWAP":
			//			var first = this.pop_number();
			//			var second = this.pop_number();
			//			this.push(first);
			//			this.push(second);
			//			return 1;
			//		case "ROLL":
			//			var count = this.pop_number();
			//			var value = this.pop_number();
			//			if (count > this.stack.length)
			//				throw new Error("Tried rolling back " + count + " places, but " +
			//						"only " + this.stack.length + " items are in the stack.");
			//			Stack temp = new Stack();
			//			for (var i = 0; i < count; i ++)
			//				temp.push(this.stack.pop());
			//			this.stack.push(value);
			//			for (var i = 0; i < count; i ++)
			//				this.stack.push(temp.pop());
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
			//			var v = this.pop_variable();
			//			if (v.name == "HISTORY") {
			//				var value = this.pop_number();
			//				this.history_index = value;
			//			} else if (v.name == "PROBE") {
			//				var value = this.pop_variable();
			//				this.probe_variable = value;
			//			} else {
			//				var value = this.pop_number();
			//				this.interrupts.set_param(v.name, value);
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
		// this.trace('Go to', this.program.address_to_label[address]);
		this.ptr = address;
		return 1;
	}






	private String popVariable() {
		if (this.stack.size() == 0) {
			throw new Error("Stack empty");
		}
		Object value = this.stack.pop();
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
			//       if (this.bullet_type == "EXPLOSIVE")
			//          this.shoot("NORMAL_BULLET", value);
			//        else
			//          this.shoot(this.bullet_type + "_BULLET", value);
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
			this.shoot(this.bulletType, value);
			return;
			//		case "FRIEND":
			//			throw new Error("Teamplay not yet implemented");
			//		case "HISTORY":
			//			return;
			//		case "HELLBORE":
			//			this.shoot(name, value);
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
			//			this.checkRadarInterrupt();
			//			this.look = value;
			//			return;
			//		case "MINE":
			//			this.shoot(name, value);
			//			return;
			//		case "MISSILE":
			//			this.shoot(name, value);
			//			return;
		case "MOVEX":
			this.teleport("x", value);
			return;
		case "MOVEY":
			this.teleport("y", value);
			return;
			//		case "NUKE":
			//			this.shoot(name, value);
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
			//			this.checkRangeInterrupt();
			//			this.scan = fix360(value);
			//			return;
		case "SHIELD":
			value = Math.max(0, value);
			if (this.shield < value) {
				int cost = value - this.shield;
				if (this.energy < cost) {
					this.shield += (this.energy);
					this.energy = 0;
				} else {
					this.shield = value;
					this.energy -= cost;
				}
			} else if (this.shield > value) {
				int gain = this.shield - value;
				this.shield = value;
				this.energy = Math.min(this.energy + gain, this.maxEnergy);
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
			this.setSpeed("x", value);
			return;
		case "SPEEDY":
			this.setSpeed("y", value);
			return;
			//	      case "STUNNER":
			//	        this.shoot(name, value);
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
			//   this.registers[name] = value;
		}
	}

	int getVariable(String name) {
		// Resolve label names first. Some simpler bots have label names with the
		// same name as variables.
		//  if (name in this.program.label_to_address) {
		//    return this.program.label_to_address[name];
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
			//  return this.history[this.history_index] || 0;
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
			//  return _.indexOf(this.arena.robots, this);
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
			//   return this.do_probe();
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
			return this.vx;
		case "SPEEDY":
			return this.vy;
		case "STUNNER":
			return 0;
			// case "TEAMMATES":
			//   throw new Error("Teamplay not yet implemented");
		case "TOP":
			return 0;
		case "WALL":
			return this.touchingWall ? 1 : 0;
		case "X":
			return x;
		case "Y":
			return y;

			//   default:
			//   if (name in this.registers) {
			//      return this.registers[name];
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
		this.energy -= (int)(energy);
		int r = this.radius;
		switch (axis) {
		case "x":
			this.x = Math.max(r, Math.min(this.arena.width - r, this.x + distance));
			break;
		case "y":
			this.y = Math.max(r, Math.min(this.arena.height - r, this.y + distance));
		}

	}






	private void setSpeed(String axis, int speedParam) 
	{
		int value = speedParam;
		if (Math.abs(value) > 5)//set max speed
		{
			if(value < 0){
				value = -5;
			}
			else
			{
				value = 5;
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
		amount = Math.min(amount, this.maxEnergy);
		this.arena.shoot(this, type, amount);
		this.energy -= amount;
		// TOOD can't move and shoot
	}








	private int popNumber() {
		if (this.stack.size() == 0) {
			throw new Error("Stack empty");
		}
		Object value = this.stack.pop();
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

	//	private void pushIntstructions(String i)
	//	{
	//	 program.pushInstructions(i);
	//     program.pushLN(program.lineNumber);
	//     program.address++;
	//	}






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
		//  case "OR": return this.op_apply2(function(a, b) { return a || b ? 1 : 0 });
		}
		return 1;
	}






	protected boolean isTouching(Robot other)
	{
		return this.distanceTo(other) <= 0;
	}

	protected boolean isTouching(Projectile other)
	{
		return this.distanceTo(other) <= 0;
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
			this.interrupts.add("RADAR");
		}
	}

	protected void checkRangeInterrupt() {
		int range = arena.doRange(this);
		if (range != 0 && range <= interrupts.getParam("RANGE"))
		{
			//System.out.println(range);
			this.interrupts.add("RANGE");
		}
	}
}
