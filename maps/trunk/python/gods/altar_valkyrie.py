import Crossfire, Crossfire_Type as t

def accept(description):
    pl.Write('Valkyrie accepts your %s sacrifice!' % description)

# XXX: need to expose NROFATTACKS to Python

altar = Crossfire.WhoAmI()
pl = Crossfire.WhoIsActivator()
praying = pl.CheckArchInventory('skill_praying')
if praying and praying.Title == 'Valkyrie':

    # accept sacrifice
    obj = altar.Above
    while obj:
        if obj.Type & 0xffff == t.FLESH:
            level_factor = 0
            part_factor = 1

            if obj.Level < praying.Level / 2:
                pl.Write('Valkyrie scorns your pathetic sacrifice!')
            elif obj.Level < praying.Level:
                accept('poor')
                level_factor = 0.5
            elif obj.Level < praying.Level * 1.5:
                accept('modest')
                level_factor = 1
            elif obj.Level < praying.Level * 2:
                accept('adequate')
                level_factor = 1.5
            elif obj.Level < praying.Level * 5:
                accept('devout')
                level_factor = 2
            else:
                accept('heroic')
                level_factor = 2.5

            # heads and hearts are worth more.  Because.
            if obj.Name.endswith('head') or obj.Name.endswith('heart'):
                part_factor = 1.5

            if obj.Exp:
                # obj has stored exp, use it
                value = obj.Exp / 5 * part_factor

            else:
                # no stored exp, estimate
                # flesh with lots of resists is worth more
                res = 0
                for at in range(26):  # XXX should be NROFATTACKS
                    res += obj.GetResist(at)

                value = max(res, 10) * level_factor * part_factor

            if obj.Quantity > 1:
                obj.Quantity -= 1
            else:
                obj.Remove()
            pl.AddExp(value, 'praying')
            break
        obj = obj.Above
