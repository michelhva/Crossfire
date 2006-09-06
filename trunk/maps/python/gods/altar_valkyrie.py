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
            factor = 0
            if obj.Level < praying.Level / 2:
                pl.Write('Valkyrie scorns your pathetic sacrifice!')
            elif obj.Level < praying.Level:
                accept('poor')
                factor = 0.5
            elif obj.Level < praying.Level * 1.5:
                accept('modest')
                factor = 1
            elif obj.Level < praying.Level * 2:
                accept('adequate')
                factor = 1.5
            elif obj.Level < praying.Level * 5:
                accept('devout')
                factor = 2
            else:
                accept('heroic')
                factor = 2.5

            # heads and hearts are worth more.  Because.
            if obj.Name.endswith('head') or obj.Name.endswith('heart'):
                factor *= 1.5

            # flesh with lots of resists is worth more
            res = 0
            for at in range(26):  # XXX should be NROFATTACKS
                res += obj.GetResist(at)

            value = max(res, 10) * factor
            if obj.Quantity > 1:
                obj.Quantity -= 1
            else:
                obj.Remove()
            pl.AddExp(value, 'praying')
            break
        obj = obj.Above
