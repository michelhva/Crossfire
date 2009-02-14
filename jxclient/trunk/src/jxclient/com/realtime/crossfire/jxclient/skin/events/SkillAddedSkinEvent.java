package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillListener;
import com.realtime.crossfire.jxclient.window.GUICommandList;

/**
 * A {@link SkinEvent} that executes a {@link GUICommandList} whenever a new
 * skill has been gained.
 * @author Andreas Kirschbaum
 */
public class SkillAddedSkinEvent implements SkinEvent
{
    /**
     * The {@link GUICommandList} to execute.
     */
    private final GUICommandList commandList;

    /**
     * The {@link Skill} to monitor.
     */
    private final Skill skill;

    /**
     * The {@link SkillListener} attached to {@link #skill}.
     */
    private final SkillListener skillListener = new SkillListener()
    {
        /** {@inheritDoc} */
        @Override
        public void gainedSkill()
        {
            commandList.execute();
        }

        /** {@inheritDoc} */
        @Override
        public void lostSkill()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void changedSkill()
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute.
     * @param skill the skill to monitor
     */
    public SkillAddedSkinEvent(final GUICommandList commandList, final Skill skill)
    {
        this.commandList = commandList;
        this.skill = skill;
        skill.addSkillListener(skillListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        skill.removeSkillListener(skillListener);
    }
}
