/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.l2jmobius.gameserver.ai;

import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_CAST;
import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_INTERACT;
import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_MOVE_TO;
import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_PICK_UP;
import static org.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_REST;

import org.l2jmobius.gameserver.enums.ShotType;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.StaticObject;
import org.l2jmobius.gameserver.model.holders.SkillUseHolder;
import org.l2jmobius.gameserver.model.interfaces.ILocational;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.targets.TargetType;

public class PlayerAI extends PlayableAI
{
	private boolean _thinking; // to prevent recursive thinking
	
	private IntentionCommand _nextIntention = null;
	
	public PlayerAI(Player player)
	{
		super(player);
	}
	
	private void saveNextIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		_nextIntention = new IntentionCommand(intention, arg0, arg1);
	}
	
	@Override
	public IntentionCommand getNextIntention()
	{
		return _nextIntention;
	}
	
	/**
	 * Saves the current Intention for this PlayerAI if necessary and calls changeIntention in AbstractAI.
	 * @param intention The new Intention to set to the AI
	 * @param args The first parameter of the Intention
	 */
	@Override
	protected synchronized void changeIntention(CtrlIntention intention, Object... args)
	{
		// do nothing unless CAST intention
		// however, forget interrupted actions when starting to use an offensive skill
		if ((intention != AI_INTENTION_CAST) || ((Skill) args[0]).isBad())
		{
			_nextIntention = null;
			super.changeIntention(intention, args);
			return;
		}
		
		final Object localArg0 = args.length > 0 ? args[0] : null;
		final Object localArg1 = args.length > 1 ? args[1] : null;
		final Object globalArg0 = (_intentionArgs != null) && (_intentionArgs.length > 0) ? _intentionArgs[0] : null;
		final Object globalArg1 = (_intentionArgs != null) && (_intentionArgs.length > 1) ? _intentionArgs[1] : null;
		
		// do nothing if next intention is same as current one.
		if ((intention == _intention) && (globalArg0 == localArg0) && (globalArg1 == localArg1))
		{
			super.changeIntention(intention, args);
			return;
		}
		
		// save current intention so it can be used after cast
		saveNextIntention(_intention, globalArg0, globalArg1);
		super.changeIntention(intention, args);
	}
	
	/**
	 * Launch actions corresponding to the Event ReadyToAct.<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Launch actions corresponding to the Event Think</li>
	 * </ul>
	 */
	@Override
	protected void onEvtReadyToAct()
	{
		// Launch actions corresponding to the Event Think
		if (_nextIntention != null)
		{
			setIntention(_nextIntention._crtlIntention, _nextIntention._arg0, _nextIntention._arg1);
			_nextIntention = null;
		}
		super.onEvtReadyToAct();
	}
	
	/**
	 * Launch actions corresponding to the Event Cancel.<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Stop an AI Follow Task</li>
	 * <li>Launch actions corresponding to the Event Think</li>
	 * </ul>
	 */
	@Override
	protected void onEvtCancel()
	{
		_nextIntention = null;
		super.onEvtCancel();
	}
	
	/**
	 * Finalize the casting of a skill. This method overrides CreatureAI method.<br>
	 * <b>What it does:</b><br>
	 * Check if actual intention is set to CAST and, if so, retrieves latest intention before the actual CAST and set it as the current intention for the player.
	 */
	@Override
	protected void onEvtFinishCasting()
	{
		if (getIntention() == AI_INTENTION_CAST)
		{
			// run interrupted or next intention
			
			final IntentionCommand nextIntention = _nextIntention;
			if (nextIntention != null)
			{
				if (nextIntention._crtlIntention != AI_INTENTION_CAST) // previous state shouldn't be casting
				{
					setIntention(nextIntention._crtlIntention, nextIntention._arg0, nextIntention._arg1);
				}
				else
				{
					setIntention(AI_INTENTION_IDLE);
				}
			}
			else
			{
				// set intention to idle if skill doesn't change intention.
				setIntention(AI_INTENTION_IDLE);
			}
		}
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		super.onEvtAttacked(attacker);
		
		// Summons in defending mode defend its master when attacked.
		final Player player = _actor.asPlayer();
		if (player.hasServitors())
		{
			for (Summon summon : player.getServitors().values())
			{
				final SummonAI ai = (SummonAI) summon.getAI();
				if (ai.isDefending())
				{
					ai.defendAttack(attacker);
				}
			}
		}
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
		super.onEvtEvaded(attacker);
		
		// Summons in defending mode defend its master when attacked.
		final Player player = _actor.asPlayer();
		if (player.hasServitors())
		{
			for (Summon summon : player.getServitors().values())
			{
				final SummonAI ai = (SummonAI) summon.getAI();
				if (ai.isDefending())
				{
					ai.defendAttack(attacker);
				}
			}
		}
	}
	
	@Override
	protected void onIntentionRest()
	{
		if (getIntention() != AI_INTENTION_REST)
		{
			changeIntention(AI_INTENTION_REST);
			setTarget(null);
			clientStopMoving(null);
		}
	}
	
	@Override
	protected void onIntentionActive()
	{
		setIntention(AI_INTENTION_IDLE);
	}
	
	/**
	 * Manage the Move To Intention : Stop current Attack and Launch a Move to Location Task.<br>
	 * <br>
	 * <b><u>Actions</u> : </b>
	 * <ul>
	 * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
	 * <li>Set the Intention of this AI to AI_INTENTION_MOVE_TO</li>
	 * <li>Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet MoveToLocation (broadcast)</li>
	 * </ul>
	 */
	@Override
	protected void onIntentionMoveTo(ILocational loc)
	{
		if (getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
			clientActionFailed();
			return;
		}
		
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isAttackingNow())
		{
			clientActionFailed();
			saveNextIntention(AI_INTENTION_MOVE_TO, loc, null);
			return;
		}
		
		// Set the Intention of this AbstractAI to AI_INTENTION_MOVE_TO
		changeIntention(AI_INTENTION_MOVE_TO, loc);
		
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();
		
		// Abort the attack of the Creature and send Server->Client ActionFailed packet
		_actor.abortAttack();
		
		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet MoveToLocation (broadcast)
		moveTo(loc.getX(), loc.getY(), loc.getZ());
	}
	
	@Override
	protected void clientNotifyDead()
	{
		_clientMovingToPawnOffset = 0;
		_clientMoving = false;
		super.clientNotifyDead();
	}
	
	private void thinkAttack()
	{
		final Player player = _actor.asPlayer();
		final SkillUseHolder queuedSkill = player.getQueuedSkill();
		if (queuedSkill != null)
		{
			// Remove the skill from queue.
			player.setQueuedSkill(null, null, false, false);
			
			// Check if player has the needed MP for the queued skill.
			if (player.getCurrentMp() >= player.getStat().getMpInitialConsume(queuedSkill.getSkill()))
			{
				// Abort attack.
				player.abortAttack();
				
				// Recharge shots.
				if (!player.isChargedShot(ShotType.SOULSHOTS) && !player.isChargedShot(ShotType.BLESSED_SOULSHOTS))
				{
					player.rechargeShots(true, false, false);
				}
				
				// Use queued skill.
				player.useMagic(queuedSkill.getSkill(), queuedSkill.getItem(), queuedSkill.isCtrlPressed(), queuedSkill.isShiftPressed());
				return;
			}
		}
		
		final WorldObject target = getTarget();
		if ((target == null) || !target.isCreature())
		{
			return;
		}
		
		if (checkTargetLostOrDead(target.asCreature()))
		{
			// Notify the target
			setTarget(null);
			return;
		}
		
		if (maybeMoveToPawn(target, _actor.getPhysicalAttackRange()))
		{
			return;
		}
		
		clientStopMoving(null);
		_actor.doAutoAttack(target.asCreature());
	}
	
	private void thinkCast()
	{
		final WorldObject target = getCastTarget();
		if ((_skill.getTargetType() == TargetType.GROUND) && _actor.isPlayer())
		{
			if (maybeMoveToPosition(_actor.asPlayer().getCurrentSkillWorldPosition(), _actor.getMagicalAttackRange(_skill)))
			{
				return;
			}
		}
		else
		{
			if (checkTargetLost(target))
			{
				if (_skill.isBad() && (target != null))
				{
					// Notify the target
					setCastTarget(null);
					setTarget(null);
				}
				return;
			}
			if ((target != null) && maybeMoveToPawn(target, _actor.getMagicalAttackRange(_skill)))
			{
				return;
			}
		}
		
		// Check if target has changed.
		final WorldObject currentTarget = _actor.getTarget();
		if ((currentTarget != target) && (currentTarget != null) && (target != null))
		{
			_actor.setTarget(target);
			_actor.doCast(_skill, _item, _forceUse, _dontMove);
			_actor.setTarget(currentTarget);
			return;
		}
		
		_actor.doCast(_skill, _item, _forceUse, _dontMove);
	}
	
	private void thinkPickUp()
	{
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow())
		{
			return;
		}
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
		{
			return;
		}
		if (maybeMoveToPawn(target, 36))
		{
			return;
		}
		setIntention(AI_INTENTION_IDLE);
		getActor().doPickupItem(target);
	}
	
	private void thinkInteract()
	{
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow())
		{
			return;
		}
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
		{
			return;
		}
		if (maybeMoveToPawn(target, 36))
		{
			return;
		}
		if (!(target instanceof StaticObject))
		{
			getActor().doInteract(target.asCreature());
		}
		setIntention(AI_INTENTION_IDLE);
	}
	
	@Override
	public void onEvtThink()
	{
		if (_thinking && (getIntention() != AI_INTENTION_CAST))
		{
			return;
		}
		
		_thinking = true;
		try
		{
			if (getIntention() == AI_INTENTION_ATTACK)
			{
				thinkAttack();
			}
			else if (getIntention() == AI_INTENTION_CAST)
			{
				thinkCast();
			}
			else if (getIntention() == AI_INTENTION_PICK_UP)
			{
				thinkPickUp();
			}
			else if (getIntention() == AI_INTENTION_INTERACT)
			{
				thinkInteract();
			}
		}
		finally
		{
			_thinking = false;
		}
	}
	
	@Override
	public Player getActor()
	{
		return super.getActor().asPlayer();
	}
}
