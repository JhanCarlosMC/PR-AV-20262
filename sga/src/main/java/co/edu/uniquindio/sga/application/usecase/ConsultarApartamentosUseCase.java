package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.dto.response.ApartamentoResumenResponse;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarApartamentosUseCase {

    private final ApartamentoRepository apartamentos;

    public List<ApartamentoResumenResponse> ejecutar() {
        return apartamentos.buscarTodos().stream()
                .filter(Apartamento::estaActivo)
                .map(apartamento -> new ApartamentoResumenResponse(apartamento.getIdentificacion().valor(),
                        apartamento.getNombre(), apartamento.getDormitorios(), apartamento.getCapacidad()))
                .sorted(Comparator.comparing(ApartamentoResumenResponse::identificacion))
                .toList();
    }
}
