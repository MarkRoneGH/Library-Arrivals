package proj.markrone.library.server.services;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Arrival;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.ArrivalDAO;

import java.time.LocalDate;

public class ArrivalService extends AbstractService<Arrival> {
    private final ArrivalDAO arrivalDAO = new ArrivalDAO();

    @Override
    protected AbstractDAO<Arrival> getDao() {
        return arrivalDAO;
    }

    @Override
    public void deleteEntity(Arrival entity) throws ResponseException {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("Поступление для удаления должно иметь ID");
        Arrival existing = arrivalDAO.findById(entity.getId());
        if (existing == null) throw new ResponseException("Поступление не найдено.");
        arrivalDAO.delete(existing);
    }

    @Override
    public void updateEntity(Arrival entity) throws ResponseException {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("Поступление для обновления должно иметь ID");
        Arrival existing = arrivalDAO.findById(entity.getId());
        if (existing == null) throw new ResponseException("Поступление не найдено.");
        arrivalDAO.update(entity);
    }

    public Arrival create(Arrival arrival) {
        if (arrival == null) throw new IllegalArgumentException("Поступление не может быть null");
        if (arrival.getArrivalDate() == null) arrival.setArrivalDate(LocalDate.now());
        arrivalDAO.save(arrival);
        return arrival;
    }
}
