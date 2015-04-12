package no.dkit.android.ludum.core.game.model;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import no.dkit.android.ludum.core.game.model.body.GameBody;

public class GameContactListener implements ContactListener {
    Object userDataA;
    Object userDataB;

    public void beginContact(Contact contact) {
        if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        if (contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null) return;

        userDataA = contact.getFixtureA().getBody().getUserData();
        userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA instanceof GameBody)
            ((GameBody) userDataA).beginContact(contact);
        if (userDataB instanceof GameBody)
            ((GameBody) userDataB).beginContact(contact);
    }

    public void endContact(Contact contact) {
        if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        if (contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null) return;

        userDataA = contact.getFixtureA().getBody().getUserData();
        userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA instanceof GameBody)
            ((GameBody) userDataA).endContact(contact);
        if (userDataB instanceof GameBody)
            ((GameBody) userDataB).endContact(contact);
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
        if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        if (contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null) return;

        userDataA = contact.getFixtureA().getBody().getUserData();
        userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA instanceof GameBody)
            ((GameBody) userDataA).preSolve(contact, oldManifold);
        if (userDataB instanceof GameBody)
            ((GameBody) userDataB).preSolve(contact, oldManifold);
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        if (contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null) return;

        userDataA = contact.getFixtureA().getBody().getUserData();
        userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA instanceof GameBody)
            ((GameBody) userDataA).postSolve(contact, impulse);
        if (userDataB instanceof GameBody)
            ((GameBody) userDataB).postSolve(contact, impulse);
    }
}
