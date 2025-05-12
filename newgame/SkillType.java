package newgame;
public enum SkillType {
    AREA_ATTACK {
        @Override
        public void execute(BykinGame game) {
            game.useAOEAttack();
        }
    },
    PIERCING_SHOT {
        @Override
        public void execute(BykinGame game) {
            game.usePiercingShot();
        }
    },
    RAPID_FIRE {
        @Override
        public void execute(BykinGame game) {
            game.useRapidFire();
        }
    };

    public abstract void execute(BykinGame game);
}
