package antho.com.go4lunch.db.mapper;

public interface IMapper<From, To>
{
    To map(From from);
}