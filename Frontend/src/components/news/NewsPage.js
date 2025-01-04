
//1. Не дублируется страницы (чтобы сохранялась пагинация и поиск)



import React, { useState, useEffect } from 'react';
import { Card, Button,Container, Form, Row, Col, Pagination, Dropdown } from 'react-bootstrap';
import NewsPagination from './NewsPagination';
import './NewsPage.css'
import AddNewsModal from './AddNewsModal';
import EditNewsModal from './EditNewsModal';
import { useLocation, useNavigate } from 'react-router-dom';
import DeleteNewsModal from './DeleteNewsModal';

const BASE_URL = 'http://localhost:8082'; 

const NewsPage = () => {
  const [news, setNews] = useState([]); 
  const [search, setSearch] = useState(''); 
  const [page, setPage] = useState(1); 
  const [pageSize, setPageSize] = useState(10); 
  const [sortOrder, setSortOrder] = useState('createDate,dsc'); 
  const [error, setError] = useState(null); 
  const [totalNewsCount, setTotalNewsCount] = useState(0);


  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState (false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedNews, setSelectedNews] = useState(0);

  const location = useLocation();      // Get the current URL
  const navigate = useNavigate();        

const handleShow = () => setShowAddModal(true);
const handleClose = () => setShowAddModal(false);
const handleDeleteClose = () => setShowDeleteModal(false);

  const handleDeleteClick = (id) => {
    setShowDeleteModal(true); 
setSelectedNews(id)
  };

  const handleEditClick = (id, title, content, tags) => {
    setSelectedNews({
      id: id,
      title: title,
     content: content, 
     tags: tags ? tags.map(tag => tag.name) : []
  });  
  setShowEditModal(true);
  };

  //Function to sync URL with search state
const updateUrlParam = (param, value) => {
  const params = new URLSearchParams (location.search);
  params.set(param, value);
  navigate({ search: params.toString() }, { replace: true });
};

 //Functions for status updates and URL synchronization (search, page, pageSize, sortOrder)
const handleSearchChange = (newSearch) => {
  setSearch(newSearch);
  updateUrlParam('search', newSearch);

};

const handlePageChange = (newPage) => {
  setPage(Math.max(newPage, 1));
  updateUrlParam('page', newPage);
 
 
};

const handlePageSizeChange = (newPageSize) => {
  setPageSize(newPageSize);
  updateUrlParam('pageSize', newPageSize);

};

const handleSortOrderChange = (newSortOrder) => {
  setSortOrder(newSortOrder);
  updateUrlParam('sortOrder', newSortOrder);
  fetchNews();
};
 
const fetchNews = async () => {
  if (page < 1) return;
  try {
    const params = new URLSearchParams(location.search);
    const currentSearch = params.get('search') || '';
    const tagMatches = currentSearch.match(/#\(([^)]+)\)/g) || [];
    const tags = tagMatches.map(tag => tag.slice(2, -1));
    const searchTerms = currentSearch.replace(/#\([^)]+\)/g, '').trim();
  
    let url = `${BASE_URL}/api/v1/news?`;
    url += `page=${page}&size=${pageSize}&sortBy=${sortOrder}`;
  
    if (searchTerms || tags.length > 0) {
      let queryParams = [];
      if (tags.length > 0) {
        queryParams.push(`tag_name=${tags.map(tag => encodeURIComponent(tag)).join(',')}`);
      }
      if (searchTerms) {
        queryParams.push(`title=${encodeURIComponent(searchTerms)}`);
        queryParams.push(`content=${encodeURIComponent(searchTerms)}`);
      }
      url = `${BASE_URL}/api/v1/news/search?${queryParams.join('&')}&page=${page}&size=${pageSize}&sortBy=${sortOrder}`;
    }

    const response = await fetch(url);
    console.log(url, response);
    if (!response.ok) {
      throw new Error('Error fetching news');
    }

    const data = await response.json();
    setNews(data.newsList);
    setTotalNewsCount(data.totalNewsCount);
  } catch (error) {
    setError(error.message);
  }};



    // Initial fetch when component loads or when URL params change
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    setSearch(params.get('search') || '');
    setPage(parseInt(params.get('page')) || 1);
    setPageSize(parseInt(params.get('pageSize')) || 10);
    setSortOrder(params.get('sortOrder') || 'createDate,dsc');
fetchNews();
  }, [location.search]);
  
  
const handleKeyDown = (e) => {
  if(e.key === 'Enter'){
    e.preventDefault();
    fetchNews();
  }
};

    const totalPages = Math.ceil(totalNewsCount / pageSize);
  
  
  return (
    <Container>
    <Row className="my-4">
      <Col xs={8}>
        <Form className="d-flex">
          <Form.Control 
            type="text" 
            placeholder="Search News..." 
            value={search} 
            onChange={(e) => handleSearchChange(e.target.value)} 
            onKeyDown={(e) => handleKeyDown(e)}
          />
        
        </Form>
      </Col>
      <Col xs={4} className="text-right">
        <Button variant="primary" onClick={handleShow}>
          Add News
        </Button>
      </Col>
      <h1 className="my-4 d-flex justify-content-between align-items-center">
        News ({news.length * page} / {totalNewsCount})
        <Dropdown onSelect={(eventKey) => handleSortOrderChange(eventKey + ',dsc')}>
<Dropdown.Toggle variant="success" id="dropdown-basic">
  Sort by:
</Dropdown.Toggle>
<Dropdown.Menu>
<Dropdown.Item eventKey= "createDate">Date Created </Dropdown.Item>
<Dropdown.Item eventKey= "title"> Title </Dropdown.Item>
<Dropdown.Item eventKey= "authorModel"> Author </Dropdown.Item>
</Dropdown.Menu>
 </Dropdown>
        
        </h1>
    </Row>

    
    {error ? <div className="error">{error}</div> : null}

   
    <Row>
      {news.map((item) => (
        <Col md={4} key={item.id} className="mb-4">
          <Card>
            <Card.Body >
              <Card.Title className="text-center"><b>{item.title}</b></Card.Title>
              <Card.Text className="text-center text-muted"><small>{item.lastUpdateDate}</small></Card.Text>
              <Card.Text>{item.content}</Card.Text> 
              <Card.Text > <data className="text-muted">AUTHOR</data> <br/> {item.authorDtoResponse.name} </Card.Text> 
              
              <div className="text-muted"> TAGS <br/>
                {item.tagList.map(tag => (
                  <button key={tag.id} type="button" className="btn btn-outline-primary btn-sm btn-box-shadow me-2 mb-2" >{tag.name}</button> 
                ))}
              </div>

              <div className="mt-3">
                <Button variant="warning" className="me-2" onClick={() => handleEditClick(item.id, item.title, item.content, item.tagList)}> <i className="fa fa-pencil"></i></Button>
                <Button variant="danger" onClick={() => handleDeleteClick(item.id)}> <i className="fa fa-trash"></i></Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      ))}
    </Row>
    <Row >
  {/* Пагинация */}
  <div className="d-flex justify-content-center mb-4 mp-4">
        <NewsPagination 
          page={page} 
          totalPages={totalPages} 
          setPage={handlePageChange} 
        />
      
        <Form.Group >
          <Form.Select value={pageSize} onChange={(e) =>  handlePageSizeChange(Number(e.target.value))}>
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
         </Form.Select> 
        </Form.Group>
        </div>
     </Row>
     
     <AddNewsModal show={showAddModal} handleClose={handleClose} onNewsAdded={fetchNews} />
     <DeleteNewsModal show={showDeleteModal}  handleClose={handleDeleteClose} selectedNewsId={selectedNews} onNewsDeleted={fetchNews}></DeleteNewsModal>
     <EditNewsModal show={showEditModal}  handleClose={() => setShowEditModal(false)} selectedNewsData={selectedNews} onNewsEdited={fetchNews}> </EditNewsModal>
    </Container>
      
  );
};

export default NewsPage;