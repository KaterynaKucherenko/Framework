import React from 'react';
import { Pagination } from 'react-bootstrap';

const NewsPagination = ({ page, totalPages, setPage }) => {
  if (isNaN(totalPages) || totalPages <= 1) {
    return null;}
  const getPaginationItems = () => {
    let items = [];

  
    items.push(
      <Pagination.Item 
        key={1} 
        active={page === 1} 
        onClick={() => setPage(1)}>
        {1}
      </Pagination.Item>
    );

 
    if (page > 3) {
      items.push(<Pagination.Ellipsis key="start-ellipsis" />);
    }

   
    const startPage = Math.max(2, page - 1); 
    const endPage = Math.min(totalPages - 1, page + 1); 

    for (let i = startPage; i <= endPage; i++) {
      items.push(
        <Pagination.Item 
          key={i} 
          active={page === i} 
          onClick={() => setPage(i)}>
          {i}
        </Pagination.Item>
      );
    }

    
    if (page < totalPages - 2) {
      items.push(<Pagination.Ellipsis key="end-ellipsis" />);
    }

    
    items.push(
      <Pagination.Item 
        key={totalPages} 
        active={page === totalPages} 
        onClick={() => setPage(totalPages)}>
        {totalPages}
      </Pagination.Item>
    );

    return items;
  };

  return (
    <Pagination>
    
      <Pagination.Prev onClick={() => setPage(page - 1)} disabled={page === 1} />

     
      {getPaginationItems()}

      <Pagination.Next onClick={() => setPage(page + 1)} disabled={page === totalPages} />
    </Pagination>
  );
};

export default NewsPagination;