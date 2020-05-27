export class TimeDial {

    score: number;
    timer: any;
    timeout: (dial: TimeDial) => void;

    level: number;
    bonus = 40;
    malus = 10;

    constructor(score: number, timeout: (dial: TimeDial) => void) {
        this.score = score;
        this.timeout = timeout;
        this.level = 0;
    }

    start() {
        if (!this.timer && this.score > 0) {
            this.timer = setInterval(()=>{
                this.tick();
            }, 100);
        }
    }

    tick() {
        this.score = Math.max(0, this.score - 1);
        this.checktimeout();
    }

    checktimeout() {
        if (this.score <= 0) {
            clearInterval(this.timer);
            this.timeout(this);
        }
    }

    levelup(correct: boolean) {
        if (correct) {
            this.score += Math.max(10, this.bonus - this.level);
            this.level ++;
        } else {
            this.score = Math.max(0, this.score - this.malus - this.level);
        }
    }

}